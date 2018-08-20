package com.gafasdesol.hawkersfans.fragments;


import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gafasdesol.hawkersfans.R;

import java.util.ArrayList;
import java.util.List;

import com.gafasdesol.hawkersfans.app.App;
import com.gafasdesol.hawkersfans.constant.ConstantValues;
import com.gafasdesol.hawkersfans.customs.DialogLoader;
import com.gafasdesol.hawkersfans.models.location_model.Countries;
import com.gafasdesol.hawkersfans.models.location_model.Zones;
import com.gafasdesol.hawkersfans.models.order_model.OrderDetails;
import com.gafasdesol.hawkersfans.models.user_model.UserBilling;
import com.gafasdesol.hawkersfans.models.user_model.UserDetails;
import com.gafasdesol.hawkersfans.models.user_model.UserShipping;
import com.gafasdesol.hawkersfans.network.APIClient;
import com.gafasdesol.hawkersfans.utils.LocationHelper;
import com.gafasdesol.hawkersfans.utils.ValidateInputs;
import retrofit2.Call;
import retrofit2.Callback;


public class Billing_Address extends Fragment {
    
    View rootView;
    String customerID;
    
    Zones selectedZone;
    Countries selectedCountry;
    DialogLoader dialogLoader;
    UserBilling userBilling;
    UserShipping userShipping;
    LocationHelper locationHelper;
    
    List<Zones> zoneList;
    List<Countries> countryList;
    
    List<String> zoneNames;
    List<String> countryNames;
    ArrayAdapter<String> zoneAdapter;
    ArrayAdapter<String> countryAdapter;
    
    Button proceed_checkout_btn;
    CheckBox default_shipping_checkbox;
    EditText input_firstname, input_lastname, input_address_1, input_address_2, input_company;
    EditText input_country, input_zone, input_city, input_postcode, input_email, input_contact;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.address, container, false);

        // Set the Title of Toolbar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.billing_address));
    
        // Get the customersID and defaultAddressID from SharedPreferences
        customerID = this.getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userID", "");
    
    
        // Binding Layout Views
        input_firstname = (EditText) rootView.findViewById(R.id.firstname);
        input_lastname = (EditText) rootView.findViewById(R.id.lastname);
        input_address_1 = (EditText) rootView.findViewById(R.id.address_1);
        input_address_2 = (EditText) rootView.findViewById(R.id.address_2);
        input_company = (EditText) rootView.findViewById(R.id.company);
        input_country = (EditText) rootView.findViewById(R.id.country);
        input_zone = (EditText) rootView.findViewById(R.id.zone);
        input_city = (EditText) rootView.findViewById(R.id.city);
        input_postcode = (EditText) rootView.findViewById(R.id.postcode);
        input_email = (EditText) rootView.findViewById(R.id.email);
        input_contact = (EditText) rootView.findViewById(R.id.contact);
        proceed_checkout_btn = (Button) rootView.findViewById(R.id.save_address_btn);
        default_shipping_checkbox = (CheckBox) rootView.findViewById(R.id.default_shipping_checkbox);
    
    
        // Set KeyListener of some View to null
        input_country.setKeyListener(null);
        input_zone.setKeyListener(null);
        input_zone.setFocusableInTouchMode(false);
    
        // Set the text of Button
        proceed_checkout_btn.setText(getContext().getString(R.string.next));
    
    
        dialogLoader = new DialogLoader(getContext());
        locationHelper = new LocationHelper(getContext());
        
        userBilling = ((App) getContext().getApplicationContext()).getOrderDetails().getBilling();
        userShipping = ((App) getContext().getApplicationContext()).getOrderDetails().getShipping();
    
    
        zoneList = new ArrayList<>();
        zoneNames = new ArrayList<>();
        countryNames = new ArrayList<>();
    
        selectedZone = null;
        selectedCountry = null;
    
    
        countryList = locationHelper.getCountries();
    
        for (int i=0;  i<countryList.size();  i++) {
            countryNames.add(countryList.get(i).getName());
        }
        countryNames.add("Other");
    
        
    
        if (userBilling == null) {
            // Request User Info
            if (!ConstantValues.IS_GUEST_LOGGED_IN)
                RequestUserDetails();
        }
        else {
            // Set the Billing Address same as Shipping
            default_shipping_checkbox.setChecked(((App) getContext().getApplicationContext()).getOrderDetails().isSameAddress());
    
            input_firstname.setText(userBilling.getFirstName());
            input_lastname.setText(userBilling.getLastName());
            input_address_1.setText(userBilling.getAddress1());
            input_address_2.setText(userBilling.getAddress2());
            input_company.setText(userBilling.getCompany());
            input_city.setText(userBilling.getCity());
            input_postcode.setText(userBilling.getPostcode());
            input_email.setText(userBilling.getEmail());
            input_contact.setText(userBilling.getPhone());
    
            String zone_code = "";
            String country_code = "";
    
            if (!TextUtils.isEmpty(userBilling.getCountry())) {
                country_code = userBilling.getCountry();
                selectedCountry = locationHelper.getCountryFromCode(country_code);
                input_country.setText(selectedCountry.getName());
        
                // Get Zones of the selected Country
                zoneList.clear();
                zoneList = new ArrayList<>();
                zoneList = locationHelper.getStates(selectedCountry.getValue());
        
                zoneNames.clear();
        
                // Add the Zone Names to the zoneNames List
                for (int x=0;  x<zoneList.size();  x++){
                    zoneNames.add(zoneList.get(x).getName());
                }
                zoneNames.add("Other");
                input_zone.setFocusableInTouchMode(true);
            }
    
    
            if (!TextUtils.isEmpty(userBilling.getState())) {
                zone_code = userBilling.getState();
                selectedZone = locationHelper.getStateFromCode(country_code, zone_code);
                input_zone.setText(selectedZone.getName());
            }
            
        }
        
    
    
        // Handle Touch event of input_country EditText
        input_country.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            
                if (event.getAction() == MotionEvent.ACTION_UP) {
                
                    countryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
                    countryAdapter.addAll(countryNames);
                
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                
                    Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);
                
                    dialog_title.setText(getString(R.string.country));
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(countryAdapter);
                
                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            countryAdapter.getFilter().filter(charSequence);
                        }
                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
                
                
                    final AlertDialog alertDialog = dialog.create();
                
                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                
                    alertDialog.show();
                
                
                
                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        
                            final String selectedItem = countryAdapter.getItem(position);
                            input_country.setText(selectedItem);
                        
                            if (!selectedItem.equalsIgnoreCase("Other")) {
                            
                                for (int i=0;  i<countryList.size();  i++) {
                                    if (countryList.get(i).getName().equalsIgnoreCase(selectedItem)) {
                                    
                                        selectedCountry = countryList.get(i);
                                    
                                        // Get Zones of the selected Country
                                        zoneList.clear();
                                        zoneList = new ArrayList<>();
                                        zoneList = locationHelper.getStates(selectedCountry.getValue());
                                    
                                        zoneNames.clear();
                                    
                                        // Add the Zone Names to the zoneNames List
                                        for (int x=0;  x<zoneList.size();  x++){
                                            zoneNames.add(zoneList.get(x).getName());
                                        }
                                        zoneNames.add("Other");
                                    }
                                }
                            
                            }
                            else {
                                selectedCountry = null;
                            
                                // Get Zones of the selected Country
                                zoneList.clear();
                                zoneList = new ArrayList<>();
                            
                                zoneNames.clear();
                                zoneNames.add("Other");
                            }
                        
                            input_zone.setText("");
                            input_zone.setFocusableInTouchMode(true);
                        
                            alertDialog.dismiss();
                        }
                    });
                
                }
            
                return false;
            }
        });
    
    
        // Handle Touch event of input_zone EditText
        input_zone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            
                if (event.getAction() == MotionEvent.ACTION_UP) {
                
                    zoneAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
                    zoneAdapter.addAll(zoneNames);
                
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                
                    Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);
                
                    dialog_title.setText(getString(R.string.zone));
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(zoneAdapter);
                
                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            zoneAdapter.getFilter().filter(charSequence);
                        }
                        @Override
                        public void afterTextChanged(Editable s) {}
                    });
                
                
                    final AlertDialog alertDialog = dialog.create();
                
                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                
                    alertDialog.show();
                
                
                
                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        
                            final String selectedItem = zoneAdapter.getItem(position);
                            input_zone.setText(selectedItem);
                        
                            if (!zoneAdapter.getItem(position).equalsIgnoreCase("Other")) {
                            
                                for (int i=0;  i<zoneList.size();  i++) {
                                    if (zoneList.get(i).getName().equalsIgnoreCase(selectedItem)) {
                                    
                                        selectedZone = zoneList.get(i);
                                    }
                                }
                            }
                            else {
                                selectedZone = null;
                            }
                        
                            alertDialog.dismiss();
                        }
                    });
                
                }
            
                return false;
            }
        });

        

        // Handle the Click event of Default Shipping Address CheckBox
        default_shipping_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Check if the CheckBox is Checked
                if (isChecked) {
                    input_firstname.setText(userShipping.getFirstName());
                    input_lastname.setText(userShipping.getLastName());
                    input_address_1.setText(userShipping.getAddress1());
                    input_address_2.setText(userShipping.getAddress2());
                    input_company.setText(userShipping.getCompany());
                    input_city.setText(userShipping.getCity());
                    input_postcode.setText(userShipping.getPostcode());
    
                    input_email.setText(getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userEmail", ""));
    
                    String zone_code = "";
                    String country_code = "";
    
                    if (!TextUtils.isEmpty(userShipping.getCountry())) {
                        country_code = userShipping.getCountry();
                        selectedCountry = locationHelper.getCountryFromCode(country_code);
                        input_country.setText(selectedCountry.getName());
        
                        // Get Zones of the selected Country
                        zoneList.clear();
                        zoneList = new ArrayList<>();
                        zoneList = locationHelper.getStates(selectedCountry.getValue());
        
                        zoneNames.clear();
        
                        // Add the Zone Names to the zoneNames List
                        for (int x=0;  x<zoneList.size();  x++){
                            zoneNames.add(zoneList.get(x).getName());
                        }
                        zoneNames.add("Other");
                        input_zone.setFocusableInTouchMode(true);
                    }
    
    
                    if (!TextUtils.isEmpty(userShipping.getState())) {
                        zone_code = userShipping.getState();
                        selectedZone = locationHelper.getStateFromCode(country_code, zone_code);
                        input_zone.setText(selectedZone.getName());
                    }
                    
                }
                else {
                    if (userBilling != null) {
                        // Set the Billing Address same as Shipping
                        default_shipping_checkbox.setChecked(false);
                        
                        input_firstname.setText(userBilling.getFirstName());
                        input_lastname.setText(userBilling.getLastName());
                        input_address_1.setText(userBilling.getAddress1());
                        input_address_2.setText(userBilling.getAddress2());
                        input_company.setText(userBilling.getCompany());
                        input_city.setText(userBilling.getCity());
                        input_postcode.setText(userBilling.getPostcode());
                        input_email.setText(userBilling.getEmail());
                        input_contact.setText(userBilling.getPhone());
        
                        String zone_code = "";
                        String country_code = "";
        
                        if (!TextUtils.isEmpty(userBilling.getCountry())) {
                            country_code = userBilling.getCountry();
                            selectedCountry = locationHelper.getCountryFromCode(country_code);
                            input_country.setText(selectedCountry.getName());
            
                            // Get Zones of the selected Country
                            zoneList.clear();
                            zoneList = new ArrayList<>();
                            zoneList = locationHelper.getStates(selectedCountry.getValue());
            
                            zoneNames.clear();
            
                            // Add the Zone Names to the zoneNames List
                            for (int x=0;  x<zoneList.size();  x++){
                                zoneNames.add(zoneList.get(x).getName());
                            }
                            zoneNames.add("Other");
                            input_zone.setFocusableInTouchMode(true);
                        }
        
        
                        if (!TextUtils.isEmpty(userBilling.getState())) {
                            zone_code = userBilling.getState();
                            selectedZone = locationHelper.getStateFromCode(country_code, zone_code);
                            input_zone.setText(selectedZone.getName());
                        }
        
                    }
                    else {
                        default_shipping_checkbox.setChecked(false);
                        
                        input_firstname.setText("");
                        input_lastname.setText("");
                        input_address_1.setText("");
                        input_address_2.setText("");
                        input_company.setText("");
                        input_country.setText("");
                        input_zone.setText("");
                        input_city.setText("");
                        input_postcode.setText("");
        
                        selectedZone = null;
                        selectedCountry = null;
        
                        input_zone.setFocusableInTouchMode(false);
                    }
                }
            }
        });


        // Handle the Click event of Proceed Order Button
        proceed_checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate Address Form Inputs
                boolean isValidData = validateAddressForm();
    
                if (isValidData) {
                    // New Instance of AddressDetails
                    userBilling = new UserBilling();
        
                    userBilling.setFirstName(input_firstname.getText().toString().trim());
                    userBilling.setLastName(input_lastname.getText().toString().trim());
                    userBilling.setAddress1(input_address_1.getText().toString().trim());
                    userBilling.setAddress2(input_address_2.getText().toString().trim());
                    userBilling.setCompany(input_company.getText().toString().trim());
                    userBilling.setCity(input_city.getText().toString().trim());
                    userBilling.setPostcode(input_postcode.getText().toString().trim());
                    userBilling.setEmail(input_email.getText().toString().trim());
                    userBilling.setPhone(input_contact.getText().toString().trim());
        
                    if (selectedCountry != null) {
                        userBilling.setCountry(selectedCountry.getValue());
                    }
                    else {
                        userBilling.setCountry("");
                    }
        
                    if (selectedZone != null) {
                        userBilling.setState(selectedZone.getValue());
                    }
                    else {
                        userBilling.setState("");
                    }
        
        
                    OrderDetails orderDetails = ((App) getContext().getApplicationContext()).getOrderDetails();
                    orderDetails.setBilling(userBilling);
                    orderDetails.setSameAddress(default_shipping_checkbox.isChecked());
        
                    ((App) getContext().getApplicationContext()).setOrderDetails(orderDetails);
                    ((App) getContext().getApplicationContext()).getUserDetails().setBilling(userBilling);
        
        
                    // Navigate to Shipping_Methods Fragment
                    Fragment fragment = new Shipping_Methods();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_fragment, fragment)
                            .addToBackStack(null).commit();
        
                }
            }
        });


        return rootView;
    }
    
    
    
    //*********** Request User's Info form Server ********//
    
    public void RequestUserDetails() {
        
        dialogLoader.showProgressDialog();
        
        Call<UserDetails> call = APIClient.getInstance()
                .getUserInfo
                        (
                                customerID
                        );
        
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, retrofit2.Response<UserDetails> response) {
                
                dialogLoader.hideProgressDialog();
                
                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getBilling() != null) {
                        
                        userBilling = response.body().getBilling();
                        
                        // Set the Address details
                        input_firstname.setText(userBilling.getFirstName());
                        input_lastname.setText(userBilling.getLastName());
                        input_address_1.setText(userBilling.getAddress1());
                        input_address_2.setText(userBilling.getAddress2());
                        input_company.setText(userBilling.getCompany());
                        input_city.setText(userBilling.getCity());
                        input_postcode.setText(userBilling.getPostcode());
                        input_email.setText(userBilling.getEmail());
                        input_contact.setText(userBilling.getPhone());
                        
                        String zone_code = "";
                        String country_code = "";
                        
                        if (!TextUtils.isEmpty(userBilling.getCountry())) {
                            country_code = userBilling.getCountry();
                            selectedCountry = locationHelper.getCountryFromCode(country_code);
                            input_country.setText(selectedCountry.getName());
                            
                            // Get Zones of the selected Country
                            zoneList.clear();
                            zoneList = new ArrayList<>();
                            zoneList = locationHelper.getStates(selectedCountry.getValue());
                            
                            zoneNames.clear();
                            
                            // Add the Zone Names to the zoneNames List
                            for (int x=0;  x<zoneList.size();  x++){
                                zoneNames.add(zoneList.get(x).getName());
                            }
                            zoneNames.add("Other");
    
                            input_zone.setFocusableInTouchMode(true);
                        }
                        
                        
                        if (!TextUtils.isEmpty(userBilling.getState())) {
                            zone_code = userBilling.getState();
                            selectedZone = locationHelper.getStateFromCode(country_code, zone_code);
                            input_zone.setText(selectedZone.getName());
                        }
                    }
                    else {
                        input_email.setText(getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userEmail", ""));
    
                        // Set the Billing Address same as Shipping
                        default_shipping_checkbox.setChecked(true);
    
                        input_firstname.setText(userShipping.getFirstName());
                        input_lastname.setText(userShipping.getLastName());
                        input_address_1.setText(userShipping.getAddress1());
                        input_address_2.setText(userShipping.getAddress2());
                        input_company.setText(userShipping.getCompany());
                        input_city.setText(userShipping.getCity());
                        input_postcode.setText(userShipping.getPostcode());
    
    
                        String zone_code = "";
                        String country_code = "";
    
                        if (!TextUtils.isEmpty(userShipping.getCountry())) {
                            country_code = userShipping.getCountry();
                            selectedCountry = locationHelper.getCountryFromCode(country_code);
                            input_country.setText(selectedCountry.getName());
        
                            // Get Zones of the selected Country
                            zoneList.clear();
                            zoneList = new ArrayList<>();
                            zoneList = locationHelper.getStates(selectedCountry.getValue());
        
                            zoneNames.clear();
        
                            // Add the Zone Names to the zoneNames List
                            for (int x=0;  x<zoneList.size();  x++){
                                zoneNames.add(zoneList.get(x).getName());
                            }
                            zoneNames.add("Other");
                            input_zone.setFocusableInTouchMode(true);
                        }
    
    
                        if (!TextUtils.isEmpty(userShipping.getState())) {
                            zone_code = userShipping.getState();
                            selectedZone = locationHelper.getStateFromCode(country_code, zone_code);
                            input_zone.setText(selectedZone.getName());
                        }
                    }
                }
                else {
                    input_email.setText(getContext().getSharedPreferences("UserInfo", getContext().MODE_PRIVATE).getString("userEmail", ""));
    
                    // Set the Billing Address same as Shipping
                    default_shipping_checkbox.setChecked(true);
    
                    input_firstname.setText(userShipping.getFirstName());
                    input_lastname.setText(userShipping.getLastName());
                    input_address_1.setText(userShipping.getAddress1());
                    input_address_2.setText(userShipping.getAddress2());
                    input_company.setText(userShipping.getCompany());
                    input_city.setText(userShipping.getCity());
                    input_postcode.setText(userShipping.getPostcode());
    
    
                    String zone_code = "";
                    String country_code = "";
    
                    if (!TextUtils.isEmpty(userShipping.getCountry())) {
                        country_code = userShipping.getCountry();
                        selectedCountry = locationHelper.getCountryFromCode(country_code);
                        input_country.setText(selectedCountry.getName());
        
                        // Get Zones of the selected Country
                        zoneList.clear();
                        zoneList = new ArrayList<>();
                        zoneList = locationHelper.getStates(selectedCountry.getValue());
        
                        zoneNames.clear();
        
                        // Add the Zone Names to the zoneNames List
                        for (int x=0;  x<zoneList.size();  x++){
                            zoneNames.add(zoneList.get(x).getName());
                        }
                        zoneNames.add("Other");
                        input_zone.setFocusableInTouchMode(true);
                    }
    
    
                    if (!TextUtils.isEmpty(userShipping.getState())) {
                        zone_code = userShipping.getState();
                        selectedZone = locationHelper.getStateFromCode(country_code, zone_code);
                        input_zone.setText(selectedZone.getName());
                    }
                }
            }
            
            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(getContext(), "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    
    //*********** Validate Address Form Inputs ********//
    
    private boolean validateAddressForm() {
        if (!ValidateInputs.isValidName(input_firstname.getText().toString().trim())) {
            input_firstname.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidName(input_lastname.getText().toString().trim())) {
            input_lastname.setError(getString(R.string.invalid_last_name));
            return false;
        } else if (!ValidateInputs.isValidInput(input_address_1.getText().toString().trim())) {
            input_address_1.setError(getString(R.string.invalid_address));
            return false;
        } else if (!ValidateInputs.isIfValidInput(input_address_2.getText().toString().trim())) {
            input_address_2.setError(getString(R.string.invalid_address));
            return false;
        } else if (!ValidateInputs.isIfValidInput(input_company.getText().toString().trim())) {
            input_company.setError(getString(R.string.invalid_company));
            return false;
        } else if (!ValidateInputs.isValidInput(input_country.getText().toString().trim())) {
            input_country.setError(getString(R.string.select_country));
            return false;
        } else if (!ValidateInputs.isValidInput(input_zone.getText().toString().trim())) {
            input_zone.setError(getString(R.string.select_zone));
            return false;
        } else if (!ValidateInputs.isValidInput(input_city.getText().toString().trim())) {
            input_city.setError(getString(R.string.enter_city));
            return false;
        } else if (!ValidateInputs.isValidNumber(input_postcode.getText().toString().trim())) {
            input_postcode.setError(getString(R.string.invalid_post_code));
            return false;
        } else if (!ValidateInputs.isValidEmail(input_email.getText().toString().trim())) {
            input_email.setError(getString(R.string.invalid_email));
            return false;
        } else if (!ValidateInputs.isValidNumber(input_contact.getText().toString().trim())) {
            input_contact.setError(getString(R.string.invalid_contact));
            return false;
        } else {
            return true;
        }
    }
    
}

