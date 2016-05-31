package com.ia04nf28.colladia;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.DialogInterface;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.String;
import java.util.List;
import com.ia04nf28.colladia.model.Manager;

public class WorkspacesListActivity extends ListActivity {

    private EditText userTextInput;
    String diagramSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT,
                AbsListView.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);


        setContentView(R.layout.activity_workspaces_list);

        updateAdapter(Manager.instance(getApplicationContext()).getDiagrams());
        Manager.instance(getApplicationContext()).addOnDiagramsChangeCallback(new ObservableList.OnListChangedCallback<ObservableList<String>>() {
            @Override
            public void onChanged(ObservableList<String> sender) {
                updateAdapter(sender);
            }

            @Override
            public void onItemRangeChanged(ObservableList<String> sender, int positionStart, int itemCount) {
                updateAdapter(sender);
                System.out.println("onItemRangeChanged");
            }

            @Override
            public void onItemRangeInserted(ObservableList<String> sender, int positionStart, int itemCount) {
                updateAdapter(sender);
                System.out.println("onItemRangeInserted");
            }


            @Override
            public void onItemRangeMoved(ObservableList<String> sender, int fromPosition, int toPosition, int itemCount) {
                updateAdapter(sender);
                System.out.println("onItemRangeMoved");
            }

            @Override
            public void onItemRangeRemoved(ObservableList<String> sender, int positionStart, int itemCount) {
                updateAdapter(sender);
                System.out.println("onItemRangeRemoved");
            }

        });


        FloatingActionButton addBut = (FloatingActionButton) findViewById(R.id.addButton);
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkspacesListActivity.this);
                userTextInput = new EditText(WorkspacesListActivity.this);

                builder.setTitle("Entrez votre texte").setView(userTextInput);
                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {
                        Manager.instance(getApplicationContext()).addDiagram(userTextInput.getText().toString());
                    }
                });

                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {

                    }
                });

                builder.create().show();
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked

        Object o = l.getItemAtPosition(position);
        diagramSelected = o.toString();

        Toast.makeText(getApplicationContext() ,"clicked " +diagramSelected, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(WorkspacesListActivity.this);

        builder.setPositiveButton("Accéder", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                Manager.instance(getApplicationContext()).setCurrentDiagram(diagramSelected);
                Manager.instance(getApplicationContext()).joinWorkspace();
                Intent intent = new Intent(WorkspacesListActivity.this, DrawActivity.class);
                startActivity(intent);
            }
        });

        builder.setNeutralButton("Supprimer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkspacesListActivity.this);

                builder.setTitle("Supprimer le diagramme " + diagramSelected + " ?");

                builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {
                        Manager.instance(getApplicationContext()).removeDiagram(diagramSelected);
                    }
                });

                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface di, int i) {

                    }
                });

                builder.create().show();
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {

            }
        });

        builder.create().show();
    }

    private void updateAdapter(List<String> list){
        setListAdapter(new ArrayAdapter<String>(this,R.layout.list_workspaces,list));

    }
}
