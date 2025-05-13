package gurux.dlms.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import java.util.List;

import gurux.dlms.ui.internal.IGXSelectList;

@SuppressLint("ViewConstructor")
public class GXCheckList<T> extends ListView {
    private final IGXSelectList<T> mSelect;

    private final ArrayAdapter<T> mAdapter;

    final List<T> mValue;

    final List<T> mValues;

    private void checkSelectedItems() {
        for (T it : mValue) {
            int index = mValues.indexOf(it);
            if (index != -1) {
                setItemChecked(index, true);
            }
        }
    }

    public GXCheckList(@NonNull Context context,
                       @NonNull final List<T> value,
                       @NonNull final List<T> values,
                       final IGXSelectList<T> select) {
        super(context);
        mValues = values;
        mValue = value;
        //Remove all extra values.
        mValue.retainAll(mValues);

        mSelect = select;
        mAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_multiple_choice,
                values
        );
        setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        setAdapter(mAdapter);
        setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                T item = mValues.get(position);
                if (checked) {
                    if (!mValue.contains(item)) {
                        mValue.add(item);
                    }
                } else {
                    mValue.remove(item);
                }
                mode.setTitle(mValue.size() + getContext().getString(R.string.chosen));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.add(0, 1, 0, getContext().getString(android.R.string.ok)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                menu.add(0, 2, 1, getContext().getString(android.R.string.cancel)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == 1) {
                    try {
                        if (mSelect != null)
                        {
                            mSelect.run(mValue);
                        }
                    } catch (Exception ignored) {
                    }
                    mode.finish();
                    return true;
                }
                if (item.getItemId() == 2) {
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                setChoiceMode(ListView.CHOICE_MODE_NONE);
                checkSelectedItems();
                setOnItemLongClickListener((parent, view, position, id) -> {
                    clearChoices();
                    mAdapter.notifyDataSetChanged();
                    setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                    checkSelectedItems();
                    return true;
                });
            }
        });
        checkSelectedItems();
    }
}
