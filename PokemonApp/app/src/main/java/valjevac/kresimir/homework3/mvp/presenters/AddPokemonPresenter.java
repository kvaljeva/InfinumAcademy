package valjevac.kresimir.homework3.mvp.presenters;

import android.net.Uri;

import valjevac.kresimir.homework3.enums.ItemsType;
import valjevac.kresimir.homework3.enums.PermissionType;

public interface AddPokemonPresenter {

    void addPokemon(String name, String description, String height, String weight, Uri imageLocation,
                    int gender);

    void getPokemonTypes();

    void getPokemonMoves();

    void setSelectedItems(String[] itemsArray, boolean[] checkedItems, ItemsType itemsType);

    void checkForPermission(String[] permissions, PermissionType permissionType);

    void cancel();
}
