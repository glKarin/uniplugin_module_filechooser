package com.pengniaoyun.uniplugin_module_filechooser.utility;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.pengniaoyun.uniplugin_module_filechooser.common.MIME;

import java.io.File;

public final class FS
{
    public static final long KB = 1024L; // 1000
    public static final long MB = KB * KB;
    public static final long GB = MB * KB;
    public static final long TB = GB * KB;

    private FS() {}

    public static String FormatSize(long size)
    {
        if(size < KB)
            return size + " B";
        if(size < MB)
            return String.format("%.1f K", ((double)size / (double)KB));
        if(size < GB)
            return String.format("%.1f M", ((double)size / (double)MB));
        if(size < TB)
            return String.format("%.1f G", ((double)size / (double)GB));
        return String.format("%.1f T", ((double)size / (double)TB));
    }

    public static boolean CompareMIME(String in, String...targets)
    {
        if(TextUtils.isEmpty(in))
            return false;

        if(targets.length == 0)
            return true;

        MIME inMime = MIME.Make(in);
        if(inMime == null)
            return false;

        for (String target : targets)
        {
            MIME targetMime = MIME.Make(target);
            if(targetMime == null)
                continue;
            if(inMime.IsSame(targetMime))
                return true;
        }
        return false;
    }

    public static String GetFileSuffix(String path)
    {
        File file = new File(path);
        if (!file.exists() || file.isDirectory())
        {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith("."))
        {
            return "";
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1)
        {
            return fileName.substring(index + 1).toLowerCase();
        }
        else
        {
            return "";
        }
    }

    public static String FileMIME(String path)
    {
        String ext = MimeTypeMap.getFileExtensionFromUrl("file://" + path);
        if(TextUtils.isEmpty(ext))
            ext = GetFileSuffix(path);
        else
            ext = ext.toLowerCase();
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    public static String UriPath(final Context context, final Uri uri)
    {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Logf.w("ExternalStorage");
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                Logf.w("Download");
                final String id = DocumentsContract.getDocumentId(uri);
                try
                {
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                catch (Exception e)
                {
                    Logf.DumpException(e);
                    Uri u = Uri.parse(id);
                    if("raw".equalsIgnoreCase(u.getScheme()))
                        return u.getPath();
                    else
                    {
                        throw e;
                        // return null;
                    }
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                Logf.w("Media");

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            Logf.w("Content");
            // Return the remote address
            /*if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();*/

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Logf.w("File");
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        catch (Exception e)
        {
            Logf.DumpException(e);
            throw e;
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
