package com.goldfish_dictionary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder> implements Filterable {
    private final DatabaseHelper databaseHelper;
    private List<Vocabulary> vocabularyList = new ArrayList<>();
    public VocabularyAdapter(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public VocabularyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        if (vocabulary == null) return;
        holder.latin.setText(vocabulary.getWord());
        holder.api.setText(vocabulary.getIpa());
    }

    @Override
    public int getItemCount() {
        if (vocabularyList != null) return vocabularyList.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                List<Vocabulary> vocabularyFilterList = new ArrayList<>();
                if (!strSearch.isEmpty())
                    vocabularyFilterList = databaseHelper.getFilterVocabulary(strSearch, 5);
                FilterResults filterResults = new FilterResults();
                filterResults.values = vocabularyFilterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                vocabularyList = (List<Vocabulary>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class VocabularyViewHolder extends RecyclerView.ViewHolder {
        private TextView latin;
        private TextView api;

        public VocabularyViewHolder(@NonNull View convertView) {
            super(convertView);
            latin = (TextView) convertView.findViewById(R.id.word);
            api   = (TextView) convertView.findViewById(R.id.ipa);
        }
    }
}
