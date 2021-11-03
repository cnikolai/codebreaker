package edu.cnm.deepdive.codebreaker.model.pojo;

import androidx.room.Relation;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import java.util.List;

public class GameWithGuesses extends Game {

  @Relation(
      entity = Guess.class,
      entityColumn = "game_id",//column name, not field name
      parentColumn = "game_id" //column in game table
  ) //populate this list with all of the guesses associated with the game
  private List<Guess> guesses;

  @Override
  public List<Guess> getGuesses() {
    return guesses;
  }

  public void setGuesses(List<Guess> guesses) {
    this.guesses = guesses;
  }
}
