package com.raqueveque.searchbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var searchToolbar: Toolbar? = null
    private var searchMenu: Menu? = null
    private var itemSearch: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setSearchToolbar()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_status -> {
                Toast.makeText(this, "Home Status Click", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_search -> {
                circleReveal(
                    R.id.searchToolbar,
                    1,
                    containsOverflow = true,
                    isShow = true
                )
                itemSearch!!.expandActionView()
                true
            }
            R.id.action_settings -> {
                Toast.makeText(this, "Home Settings Click", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setSearchToolbar() {
        searchToolbar = findViewById<View>(R.id.searchToolbar) as Toolbar
        if (searchToolbar != null) {
            searchToolbar!!.inflateMenu(R.menu.menu_search)
            searchMenu = searchToolbar!!.menu
            searchToolbar!!.setNavigationOnClickListener {
                circleReveal(
                    R.id.searchToolbar,
                    1,
                    containsOverflow = true,
                    isShow = false
                )
            }
            itemSearch = searchMenu?.findItem(R.id.action_filter_search)
            MenuItemCompat.setOnActionExpandListener(
                itemSearch,
                object : MenuItemCompat.OnActionExpandListener {
                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        // Do something when collapsed
                        circleReveal(R.id.searchToolbar, 1, containsOverflow = true, isShow = false)
                        return true
                    }

                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        // Do something when expanded
                        return true
                    }
                })
            initSearchView()
        } else Log.d("toolbar", "setSearchtollbar: NULL")
    }

    @SuppressLint("SoonBlockedPrivateApi", "CutPasteId")
    fun initSearchView() {
        val searchView = searchMenu!!.findItem(R.id.action_filter_search).actionView as SearchView

        // Enable/Disable Submit button in the keyboard
        searchView.isSubmitButtonEnabled = false

        // Change search close button image
        val closeButton = searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        closeButton.setImageResource(R.drawable.ic_close)


        // set hint and the text colors
        val txtSearch = searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
        txtSearch.hint = "Buscar.."
        txtSearch.setHintTextColor(Color.GRAY)
        txtSearch.setTextColor(Color.BLACK)


        // set the cursor
        val searchTextView =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as AutoCompleteTextView
        try {
            val mCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            mCursorDrawableRes.isAccessible = true
            mCursorDrawableRes[searchTextView] =
                R.drawable.search_cursor //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (e: Exception) {
            e.printStackTrace()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                callSearch(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                callSearch(newText)
                return true
            }

            fun callSearch(query: String) {
                //Do searching
                Log.i("query", "" + query)
            }
        })
    }

    @SuppressLint("PrivateResource")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun circleReveal(viewID: Int, posFromRight: Int, containsOverflow: Boolean, isShow: Boolean) {
        val myView = findViewById<View>(viewID)
        var width = myView.width
        if (posFromRight > 0) width -= posFromRight * resources
            .getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_button_min_width_material) - resources
            .getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_button_min_width_material) / 2
        if (containsOverflow) width -= resources
            .getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_button_min_width_overflow_material)
        val cx = width
        val cy = myView.height / 2
        val anim: Animator = if (isShow) ViewAnimationUtils.createCircularReveal(
            myView,
            cx,
            cy,
            0f,
            width.toFloat()
        ) else ViewAnimationUtils.createCircularReveal(myView, cx, cy, width.toFloat(), 0f)
        anim.duration = 220.toLong()

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isShow) {
                    super.onAnimationEnd(animation)
                    myView.visibility = View.INVISIBLE
                }
            }
        })

        // make the view visible and start the animation
        if (isShow) myView.visibility = View.VISIBLE

        // start the animation
        anim.start()
    }
}