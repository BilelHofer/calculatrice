package com.hofebil.calculatrice;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private View layout;
    private TextView TV_show_result;
    private EditText ET_input_number;
    private Button BT_plus;
    private Button BT_moins;
    private Button BT_fois;
    private Button BT_diviser;
    private Button BT_calculate;
    private Button BT_clear;
    private ArrayList<Double> allNumber = new ArrayList<>(Collections.emptyList());
    private ArrayList<String> allOperation = new ArrayList<>(Collections.emptyList());
    private String operationTemp;
    private String numActu = "";
    private boolean besoinPrio = false;
    private double numSauvegarderPourPrio = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.main_layout);
        TV_show_result = findViewById(R.id.main_resultat_textview);
        ET_input_number = findViewById(R.id.main_num_edittext);
        BT_plus = findViewById(R.id.main_plus_bouton);
        BT_moins = findViewById(R.id.main_moins_bouton);
        BT_fois = findViewById(R.id.main_fois_bouton);
        BT_diviser = findViewById(R.id.main_diviser_bouton);
        BT_calculate = findViewById(R.id.main_calc_bouton);
        BT_clear = findViewById(R.id.main_clear_bouton);
        statusBt(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();

        // ecoute les touche du clavier virtuelle
        ET_input_number.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // quand la touche delete est appuyer reset l'application
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)){
                    resetField();
                    return true;
                }

                // le bouton entrer
                if (keyCode == 66) {
                    BT_calculate.performClick();
                }
                return false;
            }

        });

        ET_input_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // recupere le dernier caractère
                String newCara = String.valueOf(editable.toString().charAt(editable.length()-1));
                if (isNumeric(newCara) || newCara.equals(".")) {
                    statusBt(true);
                    numActu += newCara;
                } else if (newCara.equals(" ") || newCara.equals(",")){
                    resetField();
                }
            }
        });

        /* addition */
        BT_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sauvNumber();
                ajoutOperateur("+");
                ET_input_number.setText(ET_input_number.getText() + "+");
                ET_input_number.setSelection(ET_input_number.length());
                statusBt(false);
            }
        });

        /* soustraction */
        BT_moins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sauvNumber();
                ajoutOperateur("-");
                ET_input_number.setText(ET_input_number.getText() + "-");
                ET_input_number.setSelection(ET_input_number.length());
                statusBt(false);
            }
        });

        /* division */
        BT_diviser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sauvNumber();
                ajoutOperateur("/");
                ET_input_number.setText(ET_input_number.getText() + "/");
                ET_input_number.setSelection(ET_input_number.length());
                statusBt(false);
            }
        });

        /* multiplication */
        BT_fois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sauvNumber();
                ajoutOperateur("*");
                ET_input_number.setText(ET_input_number.getText() + "*");
                ET_input_number.setSelection(ET_input_number.length());
                statusBt(false);
            }
        });

        /* calculer */
        BT_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sauvNumber();
                if (allOperation.size() <= 0)
                    TV_show_result.setText(String.valueOf(allNumber.get(0)));
                else
                    calculResult();
            }
        });

        /* clear */
        BT_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetField();
            }
        });
    }

    /**
     * met l'opérateur voulu au text view calc
     * @param operateur l'opérateur voulu
     */
    private void ajoutOperateur(String operateur) {
        // effectuer directement l'opération pour crée une priorité
        if (operateur.equals("*") || operateur.equals("/")) {
            int i = allNumber.size() - 1;
            numSauvegarderPourPrio = allNumber.get(i);
            allNumber.remove(i);
            besoinPrio = true;
            operationTemp = operateur;
        } else {
            allOperation.add(operateur);
        }
    }

    /**
     * les nombre entre son sauvegarder dans une liste
     */
    private void sauvNumber() {
        try {
            if (besoinPrio) {
                double temp = Double.parseDouble(numActu);
                switch (operationTemp) {
                    case "*": temp *= numSauvegarderPourPrio;
                        break;
                    case "/": temp = numSauvegarderPourPrio /= temp;
                        break;
                }
                allNumber.add(temp);
                besoinPrio = false;
            } else {
                allNumber.add(Double.parseDouble(numActu));
            }
            numActu = "";
        } catch (Exception e) {
            if (!numActu.equals("")) {
                afficheSnakBar(R.string.erreur_message);
                numActu = "";
            }
        }
    }

    /**
     * calcul le resultat
     */
    @SuppressLint("SetTextI18n")
    private void calculResult() {
        int count = 0;
        double number = allNumber.get(0);
        for (Double db : allNumber) {
            if (count != 0){
                if (allNumber.size() != count) {
                    switch (allOperation.get(count - 1)) {
                        case "+":
                            number += db;
                            break;
                        case "-":
                            number -= db;
                            break;
                        case "/":
                            number /= db;
                            break;
                        case "*":
                            number *= db;
                            break;
                    }
                }
            }
            count++;
        }
        TV_show_result.setText(Double.toString(number));
    }

    /**
     * reset l'application
     */
    private void resetField() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    /**
     * affiche une snackbar
     * @param message le message a mettre dans la snackBar
     */
    private void afficheSnakBar(int message) {
        Snackbar snack = Snackbar.make(layout,message, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snack.show();
    }

    /**
     * change le status des bouton
     * @param status le type du status
     */
    private void statusBt(boolean status) {
        BT_calculate.setEnabled(status);
        BT_fois.setEnabled(status);
        BT_moins.setEnabled(status);
        BT_diviser.setEnabled(status);
        BT_plus.setEnabled(status);
    }

    /**
     * test un str et vérifi si c'est un numeric
     * @param strNum le str a tester
     * @return true si c'est un numeric, sinon false
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }

        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}