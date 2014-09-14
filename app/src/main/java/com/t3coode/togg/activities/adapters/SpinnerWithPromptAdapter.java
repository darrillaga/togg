package com.t3coode.togg.activities.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.t3coode.togg.R;

/**
 * Decorator Adapter to allow a Spinner to show a 'Nothing Selected...'
 * initially displayed instead of the first choice in the Adapter.
 */
public class SpinnerWithPromptAdapter implements SpinnerAdapter, ListAdapter {

    protected static final int EXTRA = 1;
    protected String promptMessage;
    protected SpinnerAdapter adapter;
    protected Context context;
    protected int nothingSelectedLayout;
    protected int nothingSelectedDropdownLayout;
    protected LayoutInflater layoutInflater;
    private boolean isZeroStateAvailable;

    public static int normalizePosition(int position) {
        return position + EXTRA;
    }

    /**
     * Use this constructor to have NO 'Select One...' item, instead use the
     * standard prompt or nothing at all.
     * 
     * @param spinnerAdapter
     *            wrapped Adapter.
     * @param nothingSelectedLayout
     *            layout for nothing selected, perhaps you want text grayed out
     *            like a prompt...
     * @param context
     */
    public SpinnerWithPromptAdapter(SpinnerAdapter spinnerAdapter,
            int nothingSelectedLayout, Context context) {

        this(spinnerAdapter, nothingSelectedLayout, View.NO_ID, context);
    }

    public SpinnerWithPromptAdapter(SpinnerAdapter spinnerAdapter,
            String promptMessage, Context context) {

        this(spinnerAdapter, View.NO_ID, View.NO_ID, context);
        setPromptMessage(promptMessage);
    }

    /**
     * Use this constructor to Define your 'Select One...' layout as the first
     * row in the returned choices. If you do this, you probably don't want a
     * prompt on your spinner or it'll have two 'Select' rows.
     * 
     * @param spinnerAdapter
     *            wrapped Adapter. Should probably return false for isEnabled(0)
     * @param nothingSelectedLayout
     *            layout for nothing selected, perhaps you want text grayed out
     *            like a prompt...
     * @param nothingSelectedDropdownLayout
     *            layout for your 'Select an Item...' in the dropdown.
     * @param context
     */
    public SpinnerWithPromptAdapter(SpinnerAdapter spinnerAdapter,
            int nothingSelectedLayout, int nothingSelectedDropdownLayout,
            Context context) {
        this.adapter = spinnerAdapter;
        this.context = context;
        this.nothingSelectedLayout = nothingSelectedLayout;
        this.nothingSelectedDropdownLayout = nothingSelectedDropdownLayout;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        // This provides the View for the Selected Item in the Spinner, not
        // the dropdown (unless dropdownView is not set).
        if (position == 0) {

            View v = null;
            if (nothingSelectedLayout == View.NO_ID) {
                v = defaultView(parent);
            } else {
                v = getNothingSelectedView(parent);
            }

            return v;
        }
        if (adapter != null) {
            return adapter.getView(position - EXTRA, null, parent); // Could
                                                                    // re-use
                                                                    // the
                                                                    // convertView
                                                                    // if
                                                                    // possible.
        } else {
            return null;
        }
    }

    /**
     * View to show in Spinner with Nothing Selected Override this to do
     * something dynamic... e.g. "37 Options Found"
     * 
     * @param parent
     * @return
     */
    protected View getNothingSelectedView(ViewGroup parent) {
        return layoutInflater.inflate(nothingSelectedLayout, parent, false);
    }

    protected View getNothingSelectedDropDownView(ViewGroup parent) {
        return layoutInflater.inflate(nothingSelectedDropdownLayout, parent,
                false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (isAdapterEmpty()) {
            View v = layoutInflater.inflate(
                    android.R.layout.simple_list_item_1, parent, false);

            String emptyMessage = context.getResources().getString(
                    R.string.empty_spinner);

            ((TextView) v).setText(emptyMessage);

            return v;
        } else if (position == 0) {
            View v;

            if (isZeroStateAvailable) {
                v = getNothingSelectedDropdownView(parent);
            } else {
                v = new View(context);
                v.setVisibility(View.GONE);
            }
            return v;
        } else {
            return adapter.getDropDownView(position - EXTRA, null, parent);
        }
    }

    public View defaultView(ViewGroup parent) {
        View v = layoutInflater.inflate(android.R.layout.simple_spinner_item,
                parent, false);
        ;
        if (promptMessage == null) {
            promptMessage = context.getResources().getString(
                    R.string.default_spinner_prompt);
        }

        ((TextView) v).setText(promptMessage);

        return v;
    }

    /**
     * Override this to do something dynamic... For example, "Pick your favorite
     * of these 37".
     * 
     * @param parent
     * @return
     */
    protected View getNothingSelectedDropdownView(ViewGroup parent) {
        return layoutInflater.inflate(nothingSelectedDropdownLayout, parent,
                false);
    }

    @Override
    public int getCount() {
        int count = (adapter != null) ? adapter.getCount() : 0;
        // return count == 0 ? 0 : count + EXTRA;
        return count + EXTRA;
    }

    @Override
    public Object getItem(int position) {
        return ((position == 0) || (adapter == null)) ? null : adapter
                .getItem(position - EXTRA);
    }

    @Override
    public int getItemViewType(int position) {
        // Doesn't work!! Vote to Fix!
        // http://code.google.com/p/android/issues/detail?id=17128 -
        // Spinner does not support multiple view types
        // This method determines what is the convertView, this should
        // return 1 for pos 0 or return 0 otherwise.
        return ((position == 0) || (adapter == null)) ? getViewTypeCount()
                - EXTRA : adapter.getItemViewType(position - EXTRA);
    }

    @Override
    public int getViewTypeCount() {

        int adapterCount = (adapter != null) ? adapter.getViewTypeCount() : 0;

        return adapterCount + EXTRA;
    }

    @Override
    public long getItemId(int position) {
        return (adapter != null) ? adapter.getItemId(position - EXTRA) : -1;
    }

    @Override
    public boolean hasStableIds() {
        return adapter != null && adapter.hasStableIds();
    }

    public boolean isAdapterEmpty() {
        return adapter == null || adapter.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (adapter != null) {
            adapter.registerDataSetObserver(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (adapter != null) {
            adapter.unregisterDataSetObserver(observer);
        }
    }

    public void setZeroStateAvailable(boolean isZeroStateAvailable) {
        this.isZeroStateAvailable = isZeroStateAvailable;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return isZeroStateAvailable;
    }

    @Override
    public boolean isEnabled(int position) {
        return (position == 0 && !isZeroStateAvailable) ? false : true; // Don't
                                                                        // allow
                                                                        // the
                                                                        // 'nothing
        // selected'
        // item to be picked.
    }

    public void setPromptMessage(String message) {
        this.promptMessage = message;
    }

    public SpinnerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(SpinnerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
