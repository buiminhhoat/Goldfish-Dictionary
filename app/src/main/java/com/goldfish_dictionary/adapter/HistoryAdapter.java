package com.goldfish_dictionary.adapter;

import static com.goldfish_dictionary.utilities.Util.previewText;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.goldfish_dictionary.connection.ConnectToMySQL;
import com.goldfish_dictionary.connection.DatabaseHelper;
import com.goldfish_dictionary.R;
import com.goldfish_dictionary.object.Vocabulary;
import com.goldfish_dictionary.activity.Word;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VocabularyViewHolder> implements Filterable {
    private final DatabaseHelper databaseHelper;
    private List<Vocabulary> vocabularyList = new ArrayList<>();
    private AppCompatActivity activity;

    private String name_database;
    private String table;
    private boolean show;
    public HistoryAdapter(DatabaseHelper databaseHelper, AppCompatActivity mainActivity, String name_database, String table, boolean show) {
        this.databaseHelper = databaseHelper;
        this.activity = mainActivity;
        this.name_database = name_database;
        this.table = table;
        this.show = show;
        if (show) this.vocabularyList = databaseHelper.getAllVocabulary(table);
    }

    @NonNull
    @Override
    public VocabularyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        if (vocabulary == null) return;
        holder.latin.setText(vocabulary.getWord());
        holder.meaning.setText(previewText(vocabulary.getMeaning(), 70));
        if (Objects.equals(vocabulary.getIpa(), "")) {
            holder.detail.setText(vocabulary.getMeaning().split("\n")[0]);
        }
        else {
            holder.detail.setText(vocabulary.getIpa());
        }

        holder.imageViewTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("*");
                databaseHelper.deleteQuery("search_history",
                        new String[] {"word"},
                        new String[] {vocabulary.getWord()});
                databaseHelper.addQuery("search_history",
                        new String[] {"word_id", "is_deleted"},
                        new String[] {vocabulary.getWord_id(), "true"});

                String word_id = vocabulary.getWord_id();
                String name_database = vocabulary.getName_database();
                Thread thread = new Thread(){
                    public void run() {
                        Connection connection = ConnectToMySQL.getConnection(activity);
                        if (connection != null) {
                            try {
                                ConnectToMySQL.delete("search_history",
                                        new String[] {"word_id", "name_database"},
                                        new String[] {word_id, name_database});
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                };
                thread.start();
                vocabularyList.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    System.out.println(vocabulary.getWord() + " long");
                }
                else {
                    System.out.println(vocabulary.getWord() + " click");
                    Intent intent = new Intent(activity, Word.class);
                    intent.putExtra("WORD", vocabulary.getWord());
                    intent.putExtra("NAME_DATABASE", vocabulary.getName_database());
                    activity.startActivity(intent);
                }
            }
        });
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
                if (!strSearch.isEmpty()) {
                    vocabularyFilterList = databaseHelper.getFilterVocabulary(table, strSearch, 5);
                }
                else {
                    if (show) vocabularyFilterList = databaseHelper.getAllVocabulary(table);
                }
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

    public class VocabularyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private ItemClickListener itemClickListener;
        public RelativeLayout item;
        private TextView latin;
        private TextView detail;
        private TextView meaning;
        private ImageView imageViewTrash;

        public VocabularyViewHolder(@NonNull View convertView) {
            super(convertView);

            latin = (TextView) convertView.findViewById(R.id.word_history);
            detail   = (TextView) convertView.findViewById(R.id.ipa_history);
            item  = (RelativeLayout) convertView.findViewById(R.id.history_item);
            meaning = (TextView) convertView.findViewById(R.id.meaning_history);
            imageViewTrash = item.findViewById(R.id.imageView_trash);
            convertView.setOnClickListener(this);
            convertView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }
    }
}
