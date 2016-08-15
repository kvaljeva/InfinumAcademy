package valjevac.kresimir.homework3.mvp.views;

import valjevac.kresimir.homework3.enums.ItemsType;
import valjevac.kresimir.homework3.enums.PermissionType;
import valjevac.kresimir.homework3.enums.SetFocus;
import valjevac.kresimir.homework3.models.Pokemon;

public interface AddPokemonView extends BaseView {

    void onPokemonAddSuccess(Pokemon pokemon);

    void clearInputViews();

    void focusView(SetFocus focus);

    void getMultipleSelectionItems(String[] itemsArray, boolean[] checkedItems, String title, ItemsType itemsType);

    void displaySelectedItems(String items, ItemsType itemsType);

    void checkForPermission(String[] permissions, PermissionType permissionType);
}
