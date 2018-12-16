package com.f.events.eventapp.Presentation.ParticipantsFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.f.events.eventapp.R;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    private List<Participant> items;
    private WeakReference<Context> context;

    public void addParticipant(Participant participant) {
        items.add(participant);
        notifyItemInserted(items.size() - 1);
    }

    public ParticipantsAdapter(Context context) {
        this.context = new WeakReference<>(context);
        this.items = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context.get()).inflate(R.layout.participant_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(items.get(i));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            icon = itemView.findViewById(R.id.iv_participant_icon);
            name = itemView.findViewById(R.id.tv_participant_name);
        }

        void bind(Participant participant) {
            Picasso.get().load(participant.getImageUri())
                    .placeholder(R.drawable.ic_profile)
                    .into(icon);
            name.setText(participant.getName());
        }
    }
}
