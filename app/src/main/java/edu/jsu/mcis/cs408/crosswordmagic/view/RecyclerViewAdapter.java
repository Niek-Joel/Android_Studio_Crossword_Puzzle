package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.databinding.PuzzleRecyclerItemBinding;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private MenuActivity activity;
    private PuzzleRecyclerItemBinding binding;
    private List<PuzzleListItem> data;

    public RecyclerViewAdapter(List<PuzzleListItem> data, MenuActivity activity) {
        super();
        this.data = data;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PuzzleRecyclerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.getRoot().setOnClickListener(activity.getItemClick());
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setPuzzleItem(data.get(position));
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public PuzzleListItem getItem(int position) {
        return data.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private PuzzleListItem puzzleRecyclerItem;
        private TextView puzzleLabel;
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setPuzzleItem(PuzzleListItem puzzleItem) {
            this.puzzleRecyclerItem = puzzleItem;
        }

        public void bindData() {
            if (puzzleLabel == null) {
                puzzleLabel = (TextView) itemView.findViewById(R.id.puzzleTitleText);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(puzzleRecyclerItem.getId()).append(": ").append(puzzleRecyclerItem.getName());
            puzzleLabel.setText(sb.toString());
        }
    }
}
