package com.example.divided.mathrush;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.MyViewHolder> {

    private List<ScoreInformation> scoresList;

    ScoresAdapter(List<ScoreInformation> scoresList) {
        this.scoresList = scoresList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ScoreInformation score = scoresList.get(position);
        holder.rankingPlance.setText(Integer.toString(position + 1));
        holder.name.setText(score.getName());
        holder.round.setText("Round: " + score.getRound());
        holder.score.setText("Score: " + score.getScore());
    }

    @Override
    public int getItemCount() {
        return scoresList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, score, round, rankingPlance;

        MyViewHolder(View view) {
            super(view);
            rankingPlance = view.findViewById(R.id.mRankingPlace);
            name = view.findViewById(R.id.title);
            round = view.findViewById(R.id.genre);
            score = view.findViewById(R.id.year);
        }
    }
}