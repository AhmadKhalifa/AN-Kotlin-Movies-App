package com.android.nanodegree.kt.moviesapp.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Khalifa on 12/08/2018.
 *
 */
@Entity(tableName = "movies")
class Movie() : Parcelable {

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: Long = 0

    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    var voteAverage: Double = 0.toDouble()

    @SerializedName("title")
    @ColumnInfo(name = "title")
    var title: String? = null

    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    var posterPath: String? = null

    @SerializedName("backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    var backdropPath: String? = null

    @SerializedName("overview")
    @ColumnInfo(name = "overview")
    var overview: String? = null

    @SerializedName("release_date")
    @ColumnInfo(name = "release_date")
    var releaseDate: String? = null

    val posterPathUrl: String get() = IMAGE_BASE_URL + posterPath!!

    val backdropPathUrl: String get() = IMAGE_BASE_URL + backdropPath!!

    class DatabaseStatus {

        @ColumnInfo(name = "is_most_popular")
        var isMostPopular = false

        @ColumnInfo(name = "is_top_rated")
        var isTopRated = false

        @ColumnInfo(name = "is_favorite")
        var isFavorite = false
    }

    @Embedded
    var databaseStatus: DatabaseStatus = DatabaseStatus()

    private constructor(movieParcel: Parcel) : this() {
        id = movieParcel.readLong()
        voteAverage = movieParcel.readDouble()
        title = movieParcel.readString()
        posterPath = movieParcel.readString()
        backdropPath = movieParcel.readString()
        overview = movieParcel.readString()
        releaseDate = movieParcel.readString()
    }

    override fun equals(other: Any?) = other is Movie && other.id == id

    override fun describeContents() = 0

    override fun writeToParcel(movieParcel: Parcel, flags: Int) = with(movieParcel) {
        writeLong(id)
        writeDouble(voteAverage)
        writeString(title)
        writeString(posterPath)
        writeString(backdropPath)
        writeString(overview)
        writeString(releaseDate)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + voteAverage.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (posterPath?.hashCode() ?: 0)
        result = 31 * result + (backdropPath?.hashCode() ?: 0)
        result = 31 * result + (overview?.hashCode() ?: 0)
        result = 31 * result + (releaseDate?.hashCode() ?: 0)
        return result
    }

    open class Trailer() : Parcelable {

        @SerializedName("name")
        var name: String? = null

        @SerializedName("key")
        private var key: String? = null

        protected constructor(inParcel: Parcel) : this() {
            name = inParcel.readString()
            key = inParcel.readString()
        }

        val videoUrl: String get() = String.format(YOUTUBE_VIDEO_URL, key)

        val thumbnailImageUrl: String get() = String.format(THUMBNAIL_IMAGE_URL, key)

        override fun describeContents() = 0

        override fun writeToParcel(parcel: Parcel, i: Int) = with(parcel) {
            writeString(name)
            writeString(key)
        }

        companion object {

            private const val YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=%s"

            private const val THUMBNAIL_IMAGE_URL = "https://img.youtube.com/vi/%s/mqdefault.jpg"

            val CREATOR: Parcelable.Creator<Trailer> = object : Parcelable.Creator<Trailer> {
                override fun createFromParcel(`in`: Parcel): Trailer {
                    return Trailer(`in`)
                }

                override fun newArray(size: Int): Array<Trailer?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    open class Review() : Parcelable {

        @SerializedName("author")
        var author: String? = null

        @SerializedName("content")
        var content: String? = null

        protected constructor(inParcel: Parcel) : this() {
            author = inParcel.readString()
            content = inParcel.readString()
        }

        override fun describeContents() = 0

        override fun writeToParcel(parcel: Parcel, i: Int) = with(parcel) {
            writeString(author)
            writeString(content)
        }

        companion object {

            val CREATOR: Parcelable.Creator<Review> = object : Parcelable.Creator<Review> {
                override fun createFromParcel(`in`: Parcel): Review {
                    return Review(`in`)
                }

                override fun newArray(size: Int): Array<Review?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {

        private const val IMAGE_WIDTH = "w780"

        private const val IMAGE_BASE_URL = "http://image.tmdb.org/t/p/$IMAGE_WIDTH/"

        val CREATOR: Parcelable.Creator<Movie> = object : Parcelable.Creator<Movie> {

            override fun createFromParcel(movieParcel: Parcel): Movie {
                return Movie(movieParcel)
            }

            override fun newArray(size: Int): Array<Movie?> {
                return arrayOfNulls(size)
            }
        }
    }
}
