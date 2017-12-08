package feisabel.espertolock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by feisabel on 12/3/17.
 */

public class KeysAdapter  extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;
    String currentKey;

    public KeysAdapter(Context context, ArrayList<String> values, String currentKey) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
        this.currentKey = currentKey;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = rowView.findViewById(R.id.label);
        textView.setText(values.get(position));
        String item = values.get(position);
        if (item.equals(currentKey)) {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vpn_key_blue_24dp, 0, 0, 0);
        }
        else {
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vpn_key_white_24dp, 0, 0, 0);
        }

        return rowView;
    }
}
