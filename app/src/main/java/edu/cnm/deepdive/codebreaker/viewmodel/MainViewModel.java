package edu.cnm.deepdive.codebreaker.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.view.GameSummary;
import edu.cnm.deepdive.codebreaker.service.GameRepository;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;

public class MainViewModel extends AndroidViewModel implements LifecycleObserver {

  private final GameRepository repository;
  private final MutableLiveData<Game> game;
//  private final LiveData<List<GameSummary>> scores;
//  private final MutableLiveData<Integer> poolSize;
//  private final MutableLiveData<Integer> length;
//  private final MutableLiveData<Boolean> orderByTotalTime;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;


  public MainViewModel(@NonNull Application application) {
    super(application);
    repository = new GameRepository();
    game = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    startGame();
  }

  public LiveData<Game> getGame() {
    return game;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void startGame() {
    throwable.postValue(null);
    pending.add(
        repository
            .startGame("ABCDEF", 3)
            .subscribe(
                this.game::postValue,
                this::postThrowable
            ) //consumer of a game, consumer of a throwable; receive a throwable object and then do postThrowable object.
    );
  }

  public void submitGuess(String text) {
    throwable.postValue(null); //clears the exceptions.
    pending.add(
        repository.submitGuess(game.getValue(),text)
            .subscribe(
                game::postValue,
                this::postThrowable //if we didn't get a POST? postValue, then get a throwable
            )
    );
  }

  @OnLifecycleEvent(Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }

  private void postThrowable(Throwable throwable) {
    Log.e(getClass().getSimpleName(),throwable.getMessage(),throwable);
    this.throwable.postValue(throwable);
  }
}
