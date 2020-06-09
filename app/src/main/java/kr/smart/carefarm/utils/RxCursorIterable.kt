package kr.smart.carefarm.utils
import android.database.Cursor

class RxCursorIterable(private val mIterableCursor: Cursor) : Iterable<Cursor> {

    companion object {
        fun from(c: Cursor): RxCursorIterable {
            return RxCursorIterable(c)
        }
    }

    override fun iterator(): Iterator<Cursor> {
        return RxCursorIterator.from(
            mIterableCursor
        )
    }

    internal class RxCursorIterator(private val mCursor: Cursor) : Iterator<Cursor> {

        override fun hasNext(): Boolean {
            return !mCursor.isClosed && mCursor.moveToNext() //&& !mCursor.isLast
        }

        override fun next(): Cursor {
            return mCursor
        }

        companion object {
            fun from(cursor: Cursor): Iterator<Cursor> {
                return RxCursorIterator(cursor)
            }
        }
    }

}