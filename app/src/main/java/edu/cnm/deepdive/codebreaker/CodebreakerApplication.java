package edu.cnm.deepdive.codebreaker;

import android.app.Application;
import com.facebook.stetho.Stetho;
import edu.cnm.deepdive.codebreaker.service.CodebreakerDatabase;
import io.reactivex.schedulers.Schedulers;

public class CodebreakerApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this); //make Android apps visible with Chromium developer tools in the browser so that we can see the structure of view bojects and the database
    CodebreakerDatabase.setContext(this);
    CodebreakerDatabase
        .getInstance()
        .getGameDao()
        .delete() //act of deleting something forces room to create the database
        .subscribeOn(Schedulers.io())
        .subscribe(); //actually make this happen now.

  }
}
