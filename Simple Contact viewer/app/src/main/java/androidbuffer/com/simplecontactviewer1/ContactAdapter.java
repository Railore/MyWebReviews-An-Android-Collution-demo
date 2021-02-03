package androidbuffer.com.simplecontactviewer1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>{

    private List<ContactData> ContactDataList;

    // Constructeur
    // Les données sont passé à la classe
    public ContactAdapter(List<ContactData> ContactDataList) {
        this.ContactDataList = ContactDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view =    inflater.inflate(R.layout.contact_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // méthode qui permet de récuperer/d'affecter les noms et numéros a afficher des contacts

        ContactData model = ContactDataList.get(position);
        if (model != null){
            if (model.getContactName() != null){
                holder.name.setText(model.getContactName());
            }

            if (model.getContactNumber() != null){
                StringBuffer buffer = new StringBuffer();
                // récuperation de plusieurs numéros si il y en a
                for (String number:model.getContactNumber()){
                    buffer.append(number).append("\n");
                }
                holder.number.setText(buffer);
            }
        }
    }

    @Override
    public int getItemCount() {
        return ContactDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, number;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_diplay);
            number = itemView.findViewById(R.id.number_display);
        }

    }
}
