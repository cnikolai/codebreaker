package edu.cnm.deepdive.codebreaker.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.service.GameRepository;
import io.reactivex.disposables.CompositeDisposable;

public class PlayViewModel extends AndroidViewModel implements DefaultLifecycleObserver {

  private final GameRepository gameRepository;
  //private final SettingsRepository settingsRepository;
  private final MutableLiveData<Game> game;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;
  private final SharedPreferences preferences;
  private final String codeLengthPrefKey;
  private final String poolSizePrefKey;
  private final int codeLengthPrefDefault;
  private final int poolSizePrefDefault;
  //private int codeLength;
  //private int poolSize;
  private final String basePool;

  public PlayViewModel(@NonNull Application application) {
    super(application);
    gameRepository = new GameRepository();
    //settingsRepository = new SettingsRepository(application);
    game = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    String[] emojis = application.getResources().getStringArray(R.array.emojis);
    basePool = String.join("", emojis);
    //subscribeToSettings();
    preferences = PreferenceManager.getDefaultSharedPreferences(application);
    Resources resources = application.getResources();
    codeLengthPrefKey = resources.getString(R.string.code_length_pref_key);
    poolSizePrefKey = resources.getString(R.string.pool_size_pref_key);
    codeLengthPrefDefault = resources.getInteger(R.integer.code_length_pref_default);
    poolSizePrefDefault = resources.getInteger(R.integer.pool_size_pref_default);
    startGame();
  }

  public LiveData<Game> getGame() {
    return game;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void startGame() {
    //if (codeLength > 0 && poolSize > 0) {
    throwable.postValue(null);
    int codeLength = preferences.getInt(codeLengthPrefKey,codeLengthPrefDefault);
    int poolSize = preferences.getInt(poolSizePrefKey, poolSizePrefDefault);
    int[] poolCodePoints = basePool
        .codePoints()
        .limit(poolSize)
        .toArray();
    Game game = new Game();
    game.setPool(
        new String(poolCodePoints, 0, poolCodePoints.length)); // Read value from shared preferences
    game.setLength(codeLength); // Read value from shared preferences
    pending.add(
        gameRepository
            .save(game)
            .subscribe(
                this.game::postValue,
                this::postThrowable
            )
        //consumer of a game, consumer of a throwable; receive a throwable object and then do postThrowable object.
    );
    //}
  }

  public void submitGuess(String text) {
    throwable.postValue(null); //clears the exceptions.
    Guess guess = new Guess();
    guess.setText(text);
    //noinspection ConstantConditions
    pending.add(
        gameRepository.save(game.getValue(), guess)
            .subscribe(
                game::postValue,
                this::postThrowable //if we didn't get a POST? postValue, then get a throwable
            )
    );
  }

  @Override
  public void onDestroy(@NonNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onDestroy(owner);
    pending.clear();
  }

  private void postThrowable(Throwable throwable) {
    Log.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

  /*
  private void subscribeToSettings() {
    //we have no control over which one we get first and which one we get second.
    pending.add(
        settingsRepository
            .getCodeLengthPreference()
            .subscribe(
                (codeLength) -> {
                  this.codeLength = codeLength;
                  startGame();
                },
                this::postThrowable
            )
    );
    pending.add(
        settingsRepository
            .getPoolSizePreference()
            .subscribe(
                (poolSize) -> {
                  this.poolSize = poolSize;
                  startGame();
                },
                this::postThrowable
            )
    );
  }
   */
}
