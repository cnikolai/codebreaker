package edu.cnm.deepdive.codebreaker.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.service.GameRepository;
import io.reactivex.disposables.CompositeDisposable;

public class MainViewModel extends AndroidViewModel {

  private final GameRepository repository;
  private final MutableLiveData<Game> game;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;


  public MainViewModel(@NonNull Application application) {
    super(application);
    repository = new GameRepository();
    game = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public void startGame(String pool, int length) {
    pending.add(
        repository
            .startGame(pool, length)
            .subscribe(
                this.game::postValue,
                this::postThrowable
            ) //consumer of a game, consumer of a throwable; receive a throwable object and then do postThrowable object.
    );
  }

  private void postThrowable(Throwable throwable) {
    Log.e(getClass().getSimpleName(),throwable.getMessage(),throwable);
    this.throwable.postValue(throwable);
  }
}
