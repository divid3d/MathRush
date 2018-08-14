package com.example.divided.mathrush;

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


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, score, round, rankingPlance;

        MyViewHolder(View view) {
            super(view);
            rankingPlance = (TextView) view.findViewById(R.id.mRankingPlace);
            name = (TextView) view.findViewById(R.id.title);
            round = (TextView) view.findViewById(R.id.genre);
            score = (TextView) view.findViewById(R.id.year);
        }
    }
}