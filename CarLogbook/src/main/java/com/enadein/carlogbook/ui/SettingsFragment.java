/*
    CarLogbook.
    Copyright (C) 2014  Eugene Nadein

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.enadein.carlogbook.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.androidquery.AQuery;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.ProviderDescriptor;

public class SettingsFragment extends BaseFragment {

	private Spinner dateFormatSpinner;
	AQuery a;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.settings_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.data_fuel_type).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getMediator().showDataValues(ProviderDescriptor.DataValue.Type.FUEL);
			}
		});

		view.findViewById(R.id.data_stations).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getMediator().showDataValues(ProviderDescriptor.DataValue.Type.STATION);
			}
		});

         a = new AQuery(view);
        a.id(R.id.data_other).getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMediator().showDataValues(ProviderDescriptor.DataValue.Type.OTHERS);
            }
        });

		dateFormatSpinner = (Spinner) view.findViewById(R.id.dateFormatSpinner);
		String current = getMediator().getUnitFacade().getSetting(UnitFacade.SET_DATE_FORMAT, "0");
		if (current == null) {
			current = "0";
		}
		dateFormatSpinner.setSelection(Integer.valueOf(current));
		dateFormatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				save();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		CheckBox animEnable = (CheckBox) view.findViewById(R.id.animationEnable);
		String enable = getMediator().getUnitFacade().getSetting(UnitFacade.SET_ANIM_LIST, "1");

		animEnable.setChecked("1".equals(enable));
		animEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				getMediator().getUnitFacade().setSetings(UnitFacade.SET_ANIM_LIST, b ? "1" : "0");
				getMediator().getUnitFacade().invalidateFlags();
			}
		});

        {
            CheckBox comaCb = (CheckBox) view.findViewById(R.id.comma);
            String enableComma = getMediator().getUnitFacade().getSetting(UnitFacade.SET_COMMA, "0");

            comaCb.setChecked("1".equals(enableComma));
            comaCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    getMediator().getUnitFacade().setSetings(UnitFacade.SET_COMMA, b ? "1" : "0");
                    getMediator().getUnitFacade().invalidateFlags();
                }
            });
        }

		setupSpinner(R.id.fuelSymb, UnitFacade.SET_FRACT_FUEL, "3");
		setupSpinner(R.id.curencSymb, UnitFacade.SET_FRACT_CURRENCY, "3");
		setupSpinner(R.id.time, UnitFacade.SET_NOTIFY_TIME, "12");


		{
			CheckBox vibrateCB = (CheckBox) view.findViewById(R.id.vibrate);
			String value = getMediator().getUnitFacade().getSetting(UnitFacade.SET_NOTIFY_VIBRATE, "1");

			vibrateCB.setChecked("1".equals(value));
			vibrateCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					getMediator().getUnitFacade().setSetings(UnitFacade.SET_NOTIFY_VIBRATE, b ? "1" : "0");
					getMediator().getUnitFacade().invalidateFlags();
				}
			});
		}

		{
			CheckBox soundCB = (CheckBox) view.findViewById(R.id.sound);
			String value = getMediator().getUnitFacade().getSetting(UnitFacade.SET_NOTIFY_SOUND, "1");

			soundCB.setChecked("1".equals(value));
			soundCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					getMediator().getUnitFacade().setSetings(UnitFacade.SET_NOTIFY_SOUND, b ? "1" : "0");
					getMediator().getUnitFacade().invalidateFlags();
				}
			});
		}
	}

	private void save() {
		getMediator().getUnitFacade().setSetings(UnitFacade.SET_DATE_FORMAT, String.valueOf(dateFormatSpinner.getSelectedItemPosition()));


		getMediator().getUnitFacade().invalidateDateFormat();
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_settings);
	}


	private void setupSpinner(final int id, final String key, String defaultValue) {
		final Spinner spinner = (Spinner) a.id(id).getView();
		String current = getMediator().getUnitFacade().getSetting(key, defaultValue);
		spinner.setSelection(Integer.valueOf(current));
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				getMediator().getUnitFacade().setSetings(key, String.valueOf(spinner.getSelectedItemPosition()));
				getMediator().getUnitFacade().invalidateAll();

				if (id == R.id.time) {
					getMediator().getUnitFacade()
							.refreshNotifySystem(SettingsFragment.this.getActivity());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}
}
