package valjevac.kresimir.pokemonApp.mvp.views;

import valjevac.kresimir.pokemonApp.enums.ItemsType;
import valjevac.kresimir.pokemonApp.enums.PermissionType;
import valjevac.kresimir.pokemonApp.enums.SetFocus;
import valjevac.kresimir.pokemonApp.models.Pokemon;

public interface AddPokemonView extends BaseView {

    void onPokemonAddSuccess(Pokemon pokemon);

    void clearInputViews();

    void focusView(SetFocus focus);

    void getMultipleSelectionItems(String[] itemsArray, boolean[] checkedItems, String title, ItemsType itemsType);

    void displaySelectedItems(String items, ItemsType itemsType);

    void checkForPermission(String[] permissions, PermissionType permissionType);
}
