package com.example.serie.navigationdrawerwithfragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.Stack;


public class MainActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //フラグメント関係
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Test1Fragment test1Fragment = new Test1Fragment();
    private Test2Fragment test2Fragment = new Test2Fragment();
    private Test3Fragment test3Fragment = new Test3Fragment();

    private Toolbar toolbar;
    private NavigationView navigationView;

    //ツールバーの名前表示やフラグメント管理に使用する名前用
    private String nowFragmentName;
    private final String fragmentNameTest1 = "Test1";
    private final String fragmentNameTest2 = "Test2";
    private final String fragmentNameTest3 = "Test3";
    private Stack<String> fragmentNameStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ツールバーの初期設定
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(fragmentNameTest1);
        nowFragmentName = fragmentNameTest1;

        //ドロワー用の初期設定
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //起動時はTest1Fragmentを表示することにしておく
        FragmentManager manager = getSupportFragmentManager();
        Test1Fragment test1Fragment = new Test1Fragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.realContent, test1Fragment);
        transaction.commit();

        //ドロワーのTest1の部分を選択状態にしておく
        //こういう風にドロワーの項目を自動で選択状態にしてあげる機能の実装は面倒だが，
        //親切なUIになると思う
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        //戻るボタンを押したとき
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //ドロワーが開いていたら閉じる
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() == 0) {
            //戻るフラグメントがもうないとき

            //ダイアログを表示してホームに戻っていいかを聞く（必須ではない）
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("確認");
            alertDialog.setMessage("ホーム画面に戻ります．\nよろしいですか？");

            alertDialog.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();
                }
            });

            alertDialog.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertDialog.show();
        } else {
            fragmentManager.popBackStack();
            if (!fragmentNameStack.empty()) {
                nowFragmentName = fragmentNameStack.pop();
                toolbar.setTitle(nowFragmentName);
                if (nowFragmentName == fragmentNameTest1) {

                    //ツールバーに戻った先のフラグメントの名前を表示
                    toolbar.setTitle(fragmentNameTest1);

                    //ドロワーの該当する部分を選択状態に変更
                    navigationView.getMenu().getItem(0).setChecked(true);
                } else if (nowFragmentName == fragmentNameTest2) {
                    toolbar.setTitle(fragmentNameTest2);
                    navigationView.getMenu().getItem(1).setChecked(true);
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //ドロワーで項目をタップしたとき

        int id = item.getItemId();
        Fragment fragment = null;
        String nextFragmentName = null;

        if (id == R.id.nav_test1 && nowFragmentName != fragmentNameTest1) {
            //ここでのIDは activity_main_drawer.xml に対応している

            //このイベントで表示されるフラグメントの名前を設定
            nextFragmentName = fragmentNameTest1;
            //切り替えたいフラグメントを指定
            fragment = test1Fragment;

        } else if (id == R.id.nav_test2 && nowFragmentName != fragmentNameTest2) {
            nextFragmentName = fragmentNameTest2;
            fragment = test2Fragment;
        } else if (id == R.id.nav_test3 && nowFragmentName != fragmentNameTest3) {
            nextFragmentName = fragmentNameTest3;
            fragment = test3Fragment;
        }

        //ツールバーに名前を表示
        toolbar.setTitle(nextFragmentName);

        //フラグメントトランザクションを用意
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //フラグメントを表示したいViewのIDと，表示したいフラグメントを設定
        fragmentTransaction.replace(R.id.realContent, fragment);

        //戻るボタンを押したときに直前のフラグメントを表示するためのスタックに，
        //これから表示するフラグメントを追加
        fragmentTransaction.addToBackStack(null);

        //名前表示用のスタックにこれから表示するフラグメントの名前を追加
        fragmentNameStack.push(nowFragmentName);

        //フラグメントの切り替え
        fragmentTransaction.commit();

        //現在のフラグメントの名前を設定
        nowFragmentName = nextFragmentName;

        //ドロワーを閉じる
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
