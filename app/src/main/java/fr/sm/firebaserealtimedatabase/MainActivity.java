package fr.sm.firebaserealtimedatabase;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fr.sm.firebaserealtimedatabase.model.Author;
import fr.sm.firebaserealtimedatabase.model.Book;


public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference bookReference;
    private List<Book> bookList = new ArrayList<>();
    private ListView bookListView;
    private BookArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        bookReference = firebaseDatabase.getReference().child("books");

        bookListView = findViewById(R.id.bookListView);
        adapter = new BookArrayAdapter(this, R.layout.book_list_item, bookList);
        bookListView.setAdapter(adapter);

        //Récupération des données avec abonnement aux modifications ultérieures
        bookReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Réinitialisation de la liste des livres
                bookList.clear();

                //Boucle sur l'ensemble des noeuds
                for(DataSnapshot bookSnapshot : dataSnapshot.getChildren()){
                    //Création d'une instance de Book et hydratation avec les données du snapshot
                    Book book = bookSnapshot.getValue(Book.class);
                    //ajout du livre à la liste
                    bookList.add(book);
                }

                Log.d("MAIN", "Fin de récupération des données");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d("MAIN", "Fin de onCreate");

        //addBooks();


    }

    private void addBooks() {
        //Création d'un livre

        Author hugo = new Author("Hugo", "Victor", "Français");
        Author auster = new Author("Auster", "Paul", "Américain");

        String bookId = bookReference.push().getKey();
        Book book = new Book("Les misérables", 12.0, hugo);
        bookReference.child(bookId).setValue(book);

        bookId = bookReference.push().getKey();
        book = new Book("Ruy Blas", 12.0, hugo);
        bookReference.child(bookId).setValue(book);

        bookId = bookReference.push().getKey();
        book = new Book("New York", 11.0, auster);
        bookReference.child(bookId).setValue(book);
    }

    private class BookArrayAdapter extends ArrayAdapter<Book>{

        private Activity context;
        int resource;
        List<Book> data;

        public BookArrayAdapter(Activity context, int resource, List<Book> data) {
            super(context, resource, data);

            this.context = context;
            this.resource = resource;
            this.data = data;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = context.getLayoutInflater().inflate(
                    this.resource, parent, false);

            Book currentBook = bookList.get(position);
            TextView textView = view.findViewById(R.id.bookListText);
            textView.setText(
                            currentBook.getTitle()
                            +  " par "
                            + currentBook.getAuthor().getName()
            );

            return view;
        }
    }
}
