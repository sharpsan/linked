package com.sharpsan.linked;

import android.app.IntentService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.net.MalformedURLException;
import java.net.URL;

import io.umehara.ogmapper.OgMapper;
import io.umehara.ogmapper.domain.OgTags;
import io.umehara.ogmapper.jsoup.JsoupOgMapperFactory;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LinkedService extends IntentService {
    private static final String ACTION_FETCH_AND_COPY = "com.sharpsan.linked.action.FETCH_AND_COPY"; // fetch OG data and copy to clipboard

    private static final String EXTRA_ACTION_SHARE_KEY = "com.sharpsan.linked.extra.ACTION_SHARE_KEY";

    private final String TOAST_ALERT_COPIED_TO_CLIPBOARD = "Copied to clipboard.";

    Handler mHandler;

    public LinkedService() {
        super("LinkedService");
        mHandler = new Handler();
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFetchAndCopy(Context context, Intent param1) {
        Intent intent = new Intent(context, LinkedService.class);
        intent.setAction(ACTION_FETCH_AND_COPY);
        intent.putExtra(EXTRA_ACTION_SHARE_KEY, param1);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_AND_COPY.equals(action)) {
                final Intent param1 = intent.getParcelableExtra(EXTRA_ACTION_SHARE_KEY);
                handleActionFetchAndCopy(param1);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchAndCopy(Intent param1) {
        Intent intent = param1;
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                try {
                    handleSendText(intent); // Handle text being sent
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "MalformedURLException", Toast.LENGTH_LONG).show();
                }
            } else if (type.startsWith("image/")) {
                //handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                //handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    // handle text that is shared to the Linked app via an external intent
    void handleSendText(Intent intent) throws MalformedURLException {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            fetchOgData(sharedText);
        }
    }

    // fetch Open Graph data from a url -- in other words, fetch the description of a link
    void fetchOgData(String url) throws MalformedURLException {
        //new OpenGraphNetworking(this).execute(url);
        OgMapper ogMapper = new JsoupOgMapperFactory().build();
        URL mUrl = new URL(url);
        OgTags ogTags = ogMapper.process(mUrl);
        String ogTitle = ogTags.getTitle();
        String ogDescription = ogTags.getDescription();
        String ogUrl = ogTags.getUrl().toString();
        //// DEBUG ////
        System.out.println("title: " + ogTitle);
        System.out.println("description: " + ogDescription);
        System.out.println("url: " + ogUrl);
        ///////////////
        // create full text to be copied
        String finalText = ogTitle + "\n" + ogDescription + "\n\n" + ogUrl;
        copyToClipboard(finalText, getApplicationContext());
        // show message
        mHandler.post(new DisplayToast(this, TOAST_ALERT_COPIED_TO_CLIPBOARD));
    }

    private void copyToClipboard(String text, Context context) {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = ContextCompat.getSystemService(context, ClipboardManager.class);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("OGData_clipboard", text);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }
}
