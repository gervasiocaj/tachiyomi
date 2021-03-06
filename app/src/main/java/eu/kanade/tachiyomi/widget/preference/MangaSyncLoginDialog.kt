package eu.kanade.tachiyomi.widget.preference

import android.os.Bundle
import android.view.View
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.mangasync.MangaSyncService
import eu.kanade.tachiyomi.ui.setting.SettingsActivity
import eu.kanade.tachiyomi.util.toast
import kotlinx.android.synthetic.main.pref_account_login.view.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MangaSyncLoginDialog : LoginDialogPreference() {

    companion object {

        fun newInstance(sync: MangaSyncService): LoginDialogPreference {
            val fragment = MangaSyncLoginDialog()
            val bundle = Bundle(1)
            bundle.putInt("key", sync.id)
            fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var sync: MangaSyncService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val syncId = arguments.getInt("key")
        sync = (activity as SettingsActivity).syncManager.getService(syncId)!!
    }

    override fun setCredentialsOnView(view: View) = with(view) {
        dialog_title.text = getString(R.string.login_title, sync.name)
        username.setText(sync.getUsername())
        password.setText(sync.getPassword())
    }

    override fun checkLogin() {
        requestSubscription?.unsubscribe()

        v?.apply {
            if (username.text.length == 0 || password.text.length == 0)
                return

            login.progress = 1
            val user = username.text.toString()
            val pass = password.text.toString()

            requestSubscription = sync.login(user, pass)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ error ->
                        sync.logout()
                        login.progress = -1
                        login.setText(R.string.unknown_error)
                    }, {
                        sync.saveCredentials(user, pass)
                        dialog.dismiss()
                        context.toast(R.string.login_success)
                    })

        }
    }

}
