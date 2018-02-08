package me.gavin.app.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.app.account.LoginFragment;
import me.gavin.base.App;
import me.gavin.base.BindingActivity;
import me.gavin.base.RxBus;
import me.gavin.im.ws.R;
import me.gavin.im.ws.databinding.ActivityMainBinding;
import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends BindingActivity<ActivityMainBinding>
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        // 状态栏深色图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (savedInstanceState == null) {
            if (App.getUser() != null && App.getUser().isLogged()) {
                loadRootFragment(R.id.holder, MainFragment.newInstance());
            } else {
                loadRootFragment(R.id.holder, LoginFragment.newInstance());
            }
        }

        subscribeEvent();
        mBinding.navigation.setNavigationItemSelectedListener(this);
        mBinding.navigation.getMenu().findItem(R.id.nav_news).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mBinding.drawer.closeDrawer(Gravity.START);
        if (item.isChecked()) return true;
        switch (item.getItemId()) {
            case R.id.nav_news:
                break;
            case R.id.nav_gank:
                break;
            case R.id.nav_collection:
                break;
            case R.id.nav_about:
                break;
            case R.id.nav_test:
                break;
        }
        return false;
    }

    @Override
    public void onBackPressedSupport() {
        if (mBinding.drawer.isDrawerOpen(Gravity.START)) {
            mBinding.drawer.closeDrawer(Gravity.START);
        } else {
            super.onBackPressedSupport();
        }
    }

    private void next(SupportFragment fragment) {
        Observable.just(fragment)
                .delay(380, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(this::start);
    }

    private void subscribeEvent() {
        RxBus.get().toObservable(StartFragmentEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(event -> start(event.supportFragment));

        RxBus.get().toObservable(DrawerStateEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(event -> {
                    if (event.open) {
                        mBinding.drawer.openDrawer(Gravity.START);
                    } else {
                        mBinding.drawer.closeDrawer(Gravity.START);
                    }
                });

        RxBus.get().toObservable(DrawerEnableEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(event -> mBinding.drawer.setDrawerLockMode(event.enable
                        ? DrawerLayout.LOCK_MODE_UNLOCKED
                        : DrawerLayout.LOCK_MODE_LOCKED_CLOSED));
    }
}
