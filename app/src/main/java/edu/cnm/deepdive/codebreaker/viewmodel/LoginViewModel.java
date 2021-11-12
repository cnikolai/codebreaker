package edu.cnm.deepdive.codebreaker.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.RemoteInput.EditChoicesBeforeSending;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import edu.cnm.deepdive.codebreaker.service.GoogleSignInRepository;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class LoginViewModel extends AndroidViewModel implements DefaultLifecycleObserver {

  private final GoogleSignInRepository repository;
  private final MutableLiveData<GoogleSignInAccount> account;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending; //if clear the bucket, clears all of the tasks

  public LoginViewModel(@NonNull Application application) {
    super(application);
    repository = GoogleSignInRepository.getInstance();
    account = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    refresh(); //as soon as the view model gets loaded into memory, it will start the sign in process by refresh()
  }

  public LiveData<GoogleSignInAccount> getAccount() {
    return account;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void refresh() { //if refresh fails, don't report as an error, use has to log in again
    pending.add(
        repository
            .refresh()
            .subscribe(
                account::postValue, // (account) -> this.account.postValue(account)
                (throwable) -> account.postValue(null)
            )
    );
  }

  public void startSignIn(ActivityResultLauncher<Intent> launcher) {
    repository.startSignIn(launcher);
  }

  public void completeSignIn(ActivityResult result) {
    Disposable disposable = repository
        .completeSignIn(result)
        .subscribe(
            account::postValue,
            this::postThrowable
        );
    pending.add(disposable);
  }

  public void SignOut() {
    Disposable disposable = repository
        .signOut()
        .doFinally(() -> account.postValue(null))
        .subscribe(
            () -> {
            }, //Do nothing on success
            this::postThrowable //throw an error if it fails
        );
    pending.add(disposable);
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStop(owner);
    pending.clear();
  }

  private void postThrowable(Throwable throwable) {
    Log.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }
}
