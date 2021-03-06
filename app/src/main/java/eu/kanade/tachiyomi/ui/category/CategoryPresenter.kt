package eu.kanade.tachiyomi.ui.category

import android.os.Bundle
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Category
import eu.kanade.tachiyomi.ui.base.presenter.BasePresenter
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Presenter of CategoryActivity.
 * Contains information and data for activity.
 * Observable updates should be called from here.
 */
class CategoryPresenter : BasePresenter<CategoryActivity>() {

    /**
     * Used to connect to database
     */
    @Inject lateinit var db: DatabaseHelper

    /**
     * List containing categories
     */
    private var categories: List<Category>? = null

    companion object {
        /**
         * The id of the restartable.
         */
        final private val GET_CATEGORIES = 1
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        // Get categories as list
        restartableLatestCache(GET_CATEGORIES,
                {
                    db.getCategories().asRxObservable()
                            .doOnNext { categories -> this.categories = categories }
                            .observeOn(AndroidSchedulers.mainThread())
                }, CategoryActivity::setCategories)

        // Start get categories as list task
        start(GET_CATEGORIES)
    }


    /**
     * Create category and add it to database
     *
     * @param name name of category
     */
    fun createCategory(name: String) {
        // Create category.
        val cat = Category.create(name)

        // Set the new item in the last position.
        var max = 0
        if (categories != null) {
            for (cat2 in categories!!) {
                if (cat2.order > max) {
                    max = cat2.order + 1
                }
            }
        }
        cat.order = max

        // Insert into database.
        db.insertCategory(cat).asRxObservable().subscribe()
    }

    /**
     * Delete category from database
     *
     * @param categories list of categories
     */
    fun deleteCategories(categories: List<Category>) {
        db.deleteCategories(categories).asRxObservable().subscribe()
    }

    /**
     * Reorder categories in database
     *
     * @param categories list of categories
     */
    fun reorderCategories(categories: List<Category>) {
        for (i in categories.indices) {
            categories[i].order = i
        }

        db.insertCategories(categories).asRxObservable().subscribe()
    }

    /**
     * Rename a category
     *
     * @param category category that gets renamed
     * @param name new name of category
     */
    fun renameCategory(category: Category, name: String) {
        category.name = name
        db.insertCategory(category).asRxObservable().subscribe()
    }
}