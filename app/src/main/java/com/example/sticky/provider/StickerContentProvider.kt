package com.example.sticky.provider

import android.content.*
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.sticky.BuildConfig
import com.example.sticky.data.DatabaseProvider
import com.example.sticky.model.database.table.StickerPackTable
import java.io.File
import java.io.FileNotFoundException

class StickerContentProvider : ContentProvider() {

    companion object {
        /**
         * Do not change the strings listed below, as these are used by WhatsApp.
         */
        const val STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier"
        const val STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name"
        const val STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher"
        const val STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon"
        const val ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link"
        const val IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link"
        const val PUBLISHER_EMAIL = "sticker_pack_publisher_email"
        const val PUBLISHER_WEBSITE = "sticker_pack_publisher_website"
        const val PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website"
        const val LICENSE_AGREEMENT_WEBSITE = "sticker_pack_license_agreement_website"
        const val IMAGE_DATA_VERSION = "image_data_version"
        const val AVOID_CACHE = "whatsapp_will_not_cache_stickers"
        const val ANIMATED_STICKER_PACK = "animated_sticker_pack"

        const val STICKER_FILE_NAME_IN_QUERY = "sticker_file_name"
        const val STICKER_FILE_EMOJI_IN_QUERY = "sticker_emoji"
        const val STICKER_FILE_ACCESSIBILITY_TEXT_IN_QUERY = "sticker_accessibility_text"

        private const val METADATA = "metadata"
        private const val METADATA_CODE = 1
        private const val METADATA_CODE_FOR_SINGLE_PACK = 2

        private const val STICKERS = "stickers"
        private const val STICKERS_CODE = 3

        private const val STICKERS_ASSET = "stickers_asset"
        private const val STICKERS_ASSET_CODE = 4

        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        @JvmField
        val AUTHORITY_URI: Uri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(BuildConfig.CONTENT_PROVIDER_AUTHORITY)
            .appendPath(METADATA)
            .build()
    }

    override fun onCreate(): Boolean {
        val authority = BuildConfig.CONTENT_PROVIDER_AUTHORITY
        check(authority.startsWith(context!!.packageName)) {
            "your authority ($authority) for the content provider should start with your package name: ${context!!.packageName}"
        }

        MATCHER.addURI(authority, METADATA, METADATA_CODE)
        MATCHER.addURI(authority, "$METADATA/*", METADATA_CODE_FOR_SINGLE_PACK)
        MATCHER.addURI(authority, "$STICKERS/*", STICKERS_CODE)
        MATCHER.addURI(authority, "$STICKERS_ASSET/*/*", STICKERS_ASSET_CODE)

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d("StickerContentProvider", "query: $uri")
        return try {
            when (MATCHER.match(uri)) {
                METADATA_CODE -> {
                    Log.d("StickerContentProvider", "Matching METADATA_CODE")
                    getPackForAllStickerPacks(uri)
                }
                METADATA_CODE_FOR_SINGLE_PACK -> {
                    Log.d("StickerContentProvider", "Matching METADATA_CODE_FOR_SINGLE_PACK")
                    getCursorForSingleStickerPack(uri)
                }
                STICKERS_CODE -> {
                    Log.d("StickerContentProvider", "Matching STICKERS_CODE")
                    getStickersForAStickerPack(uri)
                }
                else -> {
                    Log.d("StickerContentProvider", "No match for URI: $uri")
                    throw IllegalArgumentException("Unknown URI: $uri")
                }
            }
        } catch (e: Exception) {
            Log.e("StickerContentProvider", "Error querying sticker packs", e)
            null
        }
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        val matchCode = MATCHER.match(uri)
        return if (matchCode == STICKERS_ASSET_CODE) {
            getImageAsset(uri)
        } else null
    }

    override fun getType(uri: Uri): String {
        return when (MATCHER.match(uri)) {
            METADATA_CODE -> "vnd.android.cursor.dir/vnd.${BuildConfig.CONTENT_PROVIDER_AUTHORITY}.$METADATA"
            METADATA_CODE_FOR_SINGLE_PACK -> "vnd.android.cursor.item/vnd.${BuildConfig.CONTENT_PROVIDER_AUTHORITY}.$METADATA"
            STICKERS_CODE -> "vnd.android.cursor.dir/vnd.${BuildConfig.CONTENT_PROVIDER_AUTHORITY}.$STICKERS"
            STICKERS_ASSET_CODE -> {
                val lastSegment = uri.lastPathSegment
                if ((lastSegment != null) && lastSegment.endsWith(".webp")) "image/webp" else "image/png"
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    private fun getPackForAllStickerPacks(uri: Uri): Cursor {
        val dao = DatabaseProvider.getDatabase(context!!).stickerPackDao
        val packs = dao.getAllStickerPacksSync()
        return getStickerPackInfo(uri, packs)
    }

    private fun getCursorForSingleStickerPack(uri: Uri): Cursor {
        val identifier = uri.lastPathSegment?.toIntOrNull() ?: return getStickerPackInfo(uri, emptyList())
        val dao = DatabaseProvider.getDatabase(context!!).stickerPackDao
        val pack = dao.getStickerPackSync(identifier)
        return getStickerPackInfo(uri, pack?.let { listOf(it) } ?: emptyList())
    }

    private fun getStickerPackInfo(uri: Uri, stickerPackList: List<StickerPackTable>): Cursor {
        Log.d("StickerContentProvider", "getStickerPackInfo for ${stickerPackList.size} packs")
        val cursor = MatrixCursor(
            arrayOf(
                STICKER_PACK_IDENTIFIER_IN_QUERY,
                STICKER_PACK_NAME_IN_QUERY,
                STICKER_PACK_PUBLISHER_IN_QUERY,
                STICKER_PACK_ICON_IN_QUERY,
                ANDROID_APP_DOWNLOAD_LINK_IN_QUERY,
                IOS_APP_DOWNLOAD_LINK_IN_QUERY,
                PUBLISHER_EMAIL,
                PUBLISHER_WEBSITE,
                PRIVACY_POLICY_WEBSITE,
                LICENSE_AGREEMENT_WEBSITE,
                IMAGE_DATA_VERSION,
                AVOID_CACHE,
                ANIMATED_STICKER_PACK
            )
        )
        for (stickerPack in stickerPackList) {
            val builder = cursor.newRow()
            builder.add(stickerPack.packId.toString())
            builder.add(stickerPack.name)
            builder.add("Sticky User") // Default publisher
            val trayIcon = stickerPack.trayIcon.substringAfterLast('/')
            builder.add(if (trayIcon.isEmpty()) "tray.webp" else trayIcon)
            builder.add("") // androidPlayStoreLink
            builder.add("") // iosAppStoreLink
            builder.add("") // publisherEmail
            builder.add("") // publisherWebsite
            builder.add("") // privacyPolicyWebsite
            builder.add("") // licenseAgreementWebsite
            builder.add(stickerPack.imageDataVersion.toString())
            builder.add("0") // avoidCache
            builder.add(if (stickerPack.isAnimated) "1" else "0")
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    private fun getStickersForAStickerPack(uri: Uri): Cursor {
        val identifierStr = uri.lastPathSegment ?: throw IllegalArgumentException("Invalid pack identifier")
        Log.d("StickerContentProvider", "getStickersForAStickerPack: $identifierStr")
        val identifier = identifierStr.toIntOrNull() ?: throw IllegalArgumentException("Invalid pack identifier")
        val stickerDao = DatabaseProvider.getDatabase(context!!).stickerDao
        val stickers = stickerDao.getStickersByPackSync(identifier)

        val cursor = MatrixCursor(
            arrayOf(
                STICKER_FILE_NAME_IN_QUERY,
                STICKER_FILE_EMOJI_IN_QUERY
            )
        )
        for (sticker in stickers) {
            val name = sticker.fileName.substringAfterLast('/')
            val emojis = if (sticker.emojis.isEmpty()) "☕" else sticker.emojis
            cursor.addRow(arrayOf(name, emojis))
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    private fun getImageAsset(uri: Uri): AssetFileDescriptor? {
        val pathSegments = uri.pathSegments
        if (pathSegments.size != 3) {
            throw IllegalArgumentException("Path segments should be 3, uri is: $uri")
        }
        val fileName = pathSegments.last()
        val identifier = pathSegments[pathSegments.size - 2]
        Log.d("StickerContentProvider", "getImageAsset: $identifier / $fileName")

        // ImageToSticker saves to "packs/packId/fileName"
        val baseDir = File(context!!.filesDir, "packs")
        val file = File(File(baseDir, identifier), fileName)

        return try {
            val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH)
        } catch (e: FileNotFoundException) {
            Log.e("StickerContentProvider", "File not found: ${file.absolutePath}", e)
            null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = throw UnsupportedOperationException()
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = throw UnsupportedOperationException()
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = throw UnsupportedOperationException()
}
