package edu.cnm.deepdive.codebreaker.model.view;

import static edu.cnm.deepdive.codebreaker.model.view.GameSummary.QUERY;

import androidx.room.ColumnInfo;
import androidx.room.DatabaseView;
import edu.cnm.deepdive.codebreaker.model.entity.Game;

@DatabaseView(value = QUERY, viewName = "game_summary") // a join or filtering of tables to show a combination of tables.
public class GameSummary extends Game {

  static final String QUERY = "SELECT\n"
      + "  g.*, s.guess_count, (s.last_guess - s.first_guess) as total_time\n"
      + "  FROM\n"
      + "  game AS g\n"
      + "  INNER JOIN (SELECT game_id, COUNT(*) as guess_count, MIN(created) as first_guess, MAX(created) as last_guess\n"
      + "  FROM guess\n"
      + "    GROUP BY game_id) AS s\n"
      + "      ON g.game_id = s.game_id";

  @ColumnInfo(name = "guess_count")
  private int guessCount;

  @ColumnInfo(name = "total_time")
  private long totalTime;

  public int getGuessCount() {
    return guessCount;
  }

  public void setGuessCount(int guessCount) {
    this.guessCount = guessCount;
  }

  public long getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(long totalTime) {
    this.totalTime = totalTime;
  }
}
