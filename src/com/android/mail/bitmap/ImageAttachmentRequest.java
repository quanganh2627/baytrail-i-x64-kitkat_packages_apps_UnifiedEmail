package com.android.mail.bitmap;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.android.bitmap.DecodeTask;
import com.android.mail.providers.Attachment;
import com.android.mail.providers.UIProvider;

import java.io.IOException;
import java.io.InputStream;

/**
 * A request object for image attachment previews.
 * <p>
 * All requests are the same size to promote bitmap reuse.
 */
public class ImageAttachmentRequest implements DecodeTask.Request {
    private final Context mContext;
    private final String lookupUri;

    public ImageAttachmentRequest(Context context, String lookupUri) {
        mContext = context;
        this.lookupUri = lookupUri;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ImageAttachmentRequest)) {
            return false;
        }
        final ImageAttachmentRequest other = (ImageAttachmentRequest) o;
        return TextUtils.equals(lookupUri, other.lookupUri);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash += 31 * hash + lookupUri.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        sb.append(super.toString());
        sb.append(" uri=");
        sb.append(lookupUri);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public AssetFileDescriptor createFd() throws IOException {
        AssetFileDescriptor result = null;
        Cursor cursor = null;
        final ContentResolver cr = mContext.getContentResolver();
        try {
            cursor = cr.query(Uri.parse(lookupUri), UIProvider.ATTACHMENT_PROJECTION, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final Attachment a = new Attachment(cursor);
                // TODO: rendition support
                result = cr.openAssetFileDescriptor(a.contentUri, "r");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    @Override
    public InputStream createInputStream() {
        return null;
    }

}