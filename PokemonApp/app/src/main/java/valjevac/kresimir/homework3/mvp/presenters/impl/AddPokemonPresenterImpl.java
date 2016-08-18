package valjevac.kresimir.homework3.mvp.presenters.impl;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import valjevac.kresimir.homework3.PokemonApplication;
import valjevac.kresimir.homework3.R;
import valjevac.kresimir.homework3.enums.ItemsType;
import valjevac.kresimir.homework3.enums.PermissionType;
import valjevac.kresimir.homework3.enums.SetFocus;
import valjevac.kresimir.homework3.helpers.ApiErrorHelper;
import valjevac.kresimir.homework3.helpers.BitmapHelper;
import valjevac.kresimir.homework3.helpers.NetworkHelper;
import valjevac.kresimir.homework3.helpers.PokemonHelper;
import valjevac.kresimir.homework3.interfaces.AddPokemonListener;
import valjevac.kresimir.homework3.models.BaseData;
import valjevac.kresimir.homework3.models.BaseResponse;
import valjevac.kresimir.homework3.models.Move;
import valjevac.kresimir.homework3.models.Pokemon;
import valjevac.kresimir.homework3.models.PokemonType;
import valjevac.kresimir.homework3.mvp.interactors.impl.AddPokemonInteractorImpl;
import valjevac.kresimir.homework3.mvp.presenters.AddPokemonPresenter;
import valjevac.kresimir.homework3.mvp.views.AddPokemonView;

public class AddPokemonPresenterImpl implements AddPokemonPresenter {

    private AddPokemonView view;

    private AddPokemonInteractorImpl interactor;

    private static final String MEDIA_TYPE = "application/image";

    private ArrayList<PokemonType> typesList;

    private ArrayList<Move> movesList;

    private boolean[] checkedMoves;

    private boolean[] checkedTypes;

    public AddPokemonPresenterImpl(AddPokemonView view) {
        this.view = view;
        this.interactor = new AddPokemonInteractorImpl();

        initializeValues();
    }

    @Override
    public void addPokemon(String name, String description, String height, String weight, Uri imageLocation,
                           int gender) {

        if (!NetworkHelper.isNetworkAvailable()) {
            view.showMessage(R.string.no_internet_conn);
            return;
        }

        if (validateInputValues(name, description, height, weight)) {
            view.showMessage(R.string.empty_field_warning);
            return;
        }

        if (!validateDecimalInput(height) || !validateDecimalInput(weight)) {
            view.showMessage(R.string.non_numeric_value_warning);
            return;
        }

        view.showProgress();

        float pokemonHeight = Float.valueOf(height);
        float pokemonWeight = Float.valueOf(weight);
        Uri location = (imageLocation == null) ? BitmapHelper.getResourceUri(R.drawable.ic_person_details) : imageLocation;

        Pokemon pokemon = new Pokemon(name, description, pokemonHeight,
                pokemonWeight, location.toString(), gender);

        int[] moves = getSelectedItemIds(checkedMoves, true);
        int[] types = getSelectedItemIds(checkedTypes, false);

        File imageFile;
        RequestBody body = null;

        String filePath = getFilePath(location);

        if (filePath != null) {

            imageFile = new File(filePath);
            body = RequestBody.create(MediaType.parse(MEDIA_TYPE), imageFile);
        }

        interactor.addPokemon(pokemon, types, moves, body, new AddPokemonListener() {
            @Override
            public void onPokemonAddSuccess(BaseResponse<BaseData<Pokemon>> body) {
                view.showMessage(R.string.pokemon_saved);
                view.clearInputViews();

                Pokemon newPokemon = body.getData().getAttributes();
                newPokemon.setId(body.getData().getId());

                view.onPokemonAddSuccess(newPokemon);
            }

            @Override
            public void onPokemonAddFail(String error) {
                view.hideProgress();

                if (ApiErrorHelper.createError(error)) {
                    view.showMessage(ApiErrorHelper.getFullError(0));
                }
            }
        });
    }

    @Override
    public void cancel() {

        if (interactor != null) {
            interactor.cancel();
        }
    }

    @Override
    public void getPokemonTypes() {
        final String[] typesArray = new String[typesList.size()];

        for (int i = 0; i < typesList.size(); i++) {
            typesArray[i] = typesList.get(i).getName();
        }

        view.getMultipleSelectionItems(typesArray, checkedTypes, "Type", ItemsType.Types);
    }

    @Override
    public void getPokemonMoves() {
        final String[] movesArray = new String[movesList.size()];

        for (int i = 0; i < movesList.size(); i++) {
            movesArray[i] = movesList.get(i).getName();
        }

        view.getMultipleSelectionItems(movesArray, checkedMoves, "Moves", ItemsType.Moves);
    }

    @Override
    public void checkForPermission(String[] permissions, PermissionType permissionType) {
        view.checkForPermission(permissions, permissionType);
    }

    @Override
    public void setSelectedItems(String[] itemsArray, boolean[] checkedItems, ItemsType itemsType) {
        String items = "";

        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                items = items + itemsArray[i] + ", ";
            }
        }

        if (!TextUtils.isEmpty(items)) {
            items = items.substring(0, items.length() - 2);
            view.displaySelectedItems(items, itemsType);
        }
        else {
            view.displaySelectedItems(items, itemsType);
        }
    }

    private String getFilePath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = PokemonApplication.getAppContext().getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            return null;
        }

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String path = cursor.getString(columnIndex);

        cursor.close();

        return path;
    }

    private boolean validateInputValues(String name, String description, String height, String weight) {
        boolean isFieldEmpty = false;

        if (TextUtils.isEmpty(name)) {
            view.focusView(SetFocus.PokemonName);
            isFieldEmpty = true;
        }
        else if (TextUtils.isEmpty(height)) {
            view.focusView(SetFocus.PokemonHeight);
            isFieldEmpty = true;
        }
        else if (TextUtils.isEmpty(weight)) {
            view.focusView(SetFocus.PokemonWeight);
            isFieldEmpty = true;
        }
        else if (TextUtils.isEmpty(description)) {
            view.focusView(SetFocus.PokemonDescription);
            isFieldEmpty = true;
        }

        return isFieldEmpty;
    }

    private boolean validateDecimalInput(String content) {
        // Promijenjeno Kocu za dusu
        Pattern pattern = Pattern.compile("^(-?0[.]\\d+)$|^(-?[1-9]+\\d*([.]\\d+)?)$|^0$");

        return pattern.matcher(content).matches();
    }

    private int[] getSelectedItemIds (boolean[] checkedItems, boolean isMoveIds) {
        int itemsCount = 0;

        for (Boolean checkedItem : checkedItems) {
            if (checkedItem) {
                itemsCount++;
            }
        }

        int[] itemIds = new int[itemsCount];

        for (int i = 0, j = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                if (isMoveIds) {
                    itemIds[j] = movesList.get(i).getId();
                    j++;
                }
                else {
                    itemIds[j] = typesList.get(i).getId();
                    j++;
                }
            }
        }

        return itemIds;
    }

    private void initializeValues() {

        typesList = PokemonHelper.getTypes();
        movesList = PokemonHelper.getMoves();

        checkedMoves = new boolean[movesList.size()];
        checkedTypes = new boolean[typesList.size()];

        for (int i = 0; i < checkedMoves.length; i++) {
            checkedMoves[i] = false;
        }

        for (int i = 0; i < checkedTypes.length; i++) {
            checkedTypes[i] = false;
        }
    }
}
