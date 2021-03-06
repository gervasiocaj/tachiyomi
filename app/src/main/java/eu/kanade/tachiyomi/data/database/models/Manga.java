package eu.kanade.tachiyomi.data.database.models;

import android.content.Context;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.io.Serializable;

import eu.kanade.tachiyomi.R;
import eu.kanade.tachiyomi.data.database.tables.MangaTable;
import eu.kanade.tachiyomi.util.UrlUtil;

@StorIOSQLiteType(table = MangaTable.TABLE)
public class Manga implements Serializable {

    @StorIOSQLiteColumn(name = MangaTable.COL_ID, key = true)
    public Long id;

    @StorIOSQLiteColumn(name = MangaTable.COL_SOURCE)
    public int source;

    @StorIOSQLiteColumn(name = MangaTable.COL_URL)
    public String url;

    @StorIOSQLiteColumn(name = MangaTable.COL_ARTIST)
    public String artist;

    @StorIOSQLiteColumn(name = MangaTable.COL_AUTHOR)
    public String author;

    @StorIOSQLiteColumn(name = MangaTable.COL_DESCRIPTION)
    public String description;

    @StorIOSQLiteColumn(name = MangaTable.COL_GENRE)
    public String genre;

    @StorIOSQLiteColumn(name = MangaTable.COL_TITLE)
    public String title;

    @StorIOSQLiteColumn(name = MangaTable.COL_STATUS)
    public int status;

    @StorIOSQLiteColumn(name = MangaTable.COL_THUMBNAIL_URL)
    public String thumbnail_url;

    @StorIOSQLiteColumn(name = MangaTable.COL_FAVORITE)
    public boolean favorite;

    @StorIOSQLiteColumn(name = MangaTable.COL_LAST_UPDATE)
    public long last_update;

    @StorIOSQLiteColumn(name = MangaTable.COL_INITIALIZED)
    public boolean initialized;

    @StorIOSQLiteColumn(name = MangaTable.COL_VIEWER)
    public int viewer;

    @StorIOSQLiteColumn(name = MangaTable.COL_CHAPTER_FLAGS)
    public int chapter_flags;

    public transient int unread;

    public transient int category;

    public static final int UNKNOWN = 0;
    public static final int ONGOING = 1;
    public static final int COMPLETED = 2;
    public static final int LICENSED = 3;

    public static final int SORT_DESC = 0x00000000;
    public static final int SORT_ASC  = 0x00000001;
    public static final int SORT_MASK = 0x00000001;

    // Generic filter that does not filter anything
    public static final int SHOW_ALL    = 0x00000000;

    public static final int SHOW_UNREAD = 0x00000002;
    public static final int SHOW_READ   = 0x00000004;
    public static final int READ_MASK   = 0x00000006;

    public static final int SHOW_DOWNLOADED     = 0x00000008;
    public static final int SHOW_NOT_DOWNLOADED = 0x00000010;
    public static final int DOWNLOADED_MASK     = 0x00000018;

    public static final int SORTING_SOURCE = 0x00000000;
    public static final int SORTING_NUMBER = 0x00000100;
    public static final int SORTING_MASK   = 0x00000100;

    public static final int DISPLAY_NAME   = 0x00000000;
    public static final int DISPLAY_NUMBER = 0x00100000;
    public static final int DISPLAY_MASK   = 0x00100000;

    public Manga() {}

    public static Manga create(String pathUrl) {
        Manga m = new Manga();
        m.url = pathUrl;
        return m;
    }

    public static Manga create(String pathUrl, int source) {
        Manga m = new Manga();
        m.url = pathUrl;
        m.source = source;
        return m;
    }

    public void setUrl(String url) {
        this.url = UrlUtil.getPath(url);
    }

    public void copyFrom(Manga other) {
        if (other.title != null)
            title = other.title;

        if (other.author != null)
            author = other.author;

        if (other.artist != null)
            artist = other.artist;

        if (other.url != null)
            url = other.url;

        if (other.description != null)
            description = other.description;

        if (other.genre != null)
            genre = other.genre;

        if (other.thumbnail_url != null)
            thumbnail_url = other.thumbnail_url;

        status = other.status;

        initialized = true;
    }

    public String getStatus(Context context) {
        switch (status) {
            case ONGOING:
                return context.getString(R.string.ongoing);
            case COMPLETED:
                return context.getString(R.string.completed);
            case LICENSED:
                return context.getString(R.string.licensed);
            default:
                return context.getString(R.string.unknown);
        }
    }

    public void setChapterOrder(int order) {
        setFlags(order, SORT_MASK);
    }

    public void setDisplayMode(int mode) {
        setFlags(mode, DISPLAY_MASK);
    }

    public void setReadFilter(int filter) {
        setFlags(filter, READ_MASK);
    }

    public void setDownloadedFilter(int filter) {
        setFlags(filter, DOWNLOADED_MASK);
    }

    public void setSorting(int sort) {
        setFlags(sort, SORTING_MASK);
    }

    private void setFlags(int flag, int mask) {
        chapter_flags = (chapter_flags & ~mask) | (flag & mask);
    }

    public boolean sortDescending() {
        return (chapter_flags & SORT_MASK) == SORT_DESC;
    }

    // Used to display the chapter's title one way or another
    public int getDisplayMode() {
        return chapter_flags & DISPLAY_MASK;
    }

    public int getReadFilter() {
        return chapter_flags & READ_MASK;
    }

    public int getDownloadedFilter() {
        return chapter_flags & DOWNLOADED_MASK;
    }

    public int getSorting() {
        return chapter_flags & SORTING_MASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Manga manga = (Manga) o;

        return url.equals(manga.url);

    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
