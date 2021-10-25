package edu.cnm.deepdive.codebreaker.controller;

import android.os.Bundle;
import android.os.TokenWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.codebreaker.adapter.GuessItemAdapter;
import edu.cnm.deepdive.codebreaker.databinding.FragmentPlayBinding;
import edu.cnm.deepdive.codebreaker.viewmodel.MainViewModel;

public class PlayFragment extends Fragment {

  private MainViewModel viewModel;
  private FragmentPlayBinding binding;

  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentPlayBinding.inflate(inflater, container, false);
    binding.submit.setOnClickListener((v) ->
        viewModel.submitGuess(binding.guess.getText().toString().trim())
    ); //compiler infers that v is a view : (View v)
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    viewModel.getThrowable().observe(getViewLifecycleOwner(), (throwable) -> {
          if (throwable != null) {
            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
          }
        }
    );//gets access to livedata from viewmodel - just observing, run contents of bucket once this object changes.
    viewModel.getGame().observe(getViewLifecycleOwner(), (game) -> {
      GuessItemAdapter adapter = new GuessItemAdapter(getContext(), game.getGuesses());// how we get a context in a fragment
      binding.guesses.setAdapter(adapter);//this adapter can tell us our guesses
      binding.guessContainer.setVisibility(game.isSolved() ? View.GONE : View.VISIBLE);
    }); //observes a game
  } //when fragment dies, then cleans up

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}