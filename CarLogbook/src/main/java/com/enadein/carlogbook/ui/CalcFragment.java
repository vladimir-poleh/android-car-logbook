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

import android.content.ContentResolver;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.ReportFacade;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;

public class CalcFragment extends BaseFragment {
    private AQuery query;
    private UnitFacade unitFacade;
    private EditText distanceValueView;
    private EditText consumptionValueView;
    private EditText priceUnitValueView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.calc, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        query = new AQuery(view);
        unitFacade = getMediator().getUnitFacade();

        unitFacade.appendDistUnit(query.id(R.id.distance).getTextView(), true);
        unitFacade.appendConsumUnit(query.id(R.id.consumption).getTextView(), true);
        unitFacade.appendCurrency(query.id(R.id.priceUnit).getTextView(), false);
        ContentResolver cr = getActivity().getContentResolver();
        long activeCarId = DBUtils.getActiveCarId(cr);
        double consumption = DBUtils.getAvgFuel(activeCarId,cr, -1, -1, unitFacade);
        double lastPriciUnit = DBUtils.getLastPriceValue(cr);

        distanceValueView = query.id(R.id.distanceValue).getEditText();

        consumptionValueView = query.id(R.id.consumptionValue).getEditText();
        consumptionValueView.setText(CommonUtils.formatFuel(consumption, unitFacade));

        priceUnitValueView = query.id(R.id.priceUnitValue).getEditText();
        priceUnitValueView.setText(CommonUtils.formatFuel(lastPriciUnit, unitFacade));

        CalcTextWatcher calcTextWatcher = new CalcTextWatcher();
        distanceValueView.addTextChangedListener(calcTextWatcher);
        consumptionValueView.addTextChangedListener(calcTextWatcher);
        priceUnitValueView.addTextChangedListener(calcTextWatcher);

        distanceValueView.setText("0");
    }

    public void calculate() {
        double dist = CommonUtils.getRawDouble(distanceValueView.getText().toString());
        double priceUnit =  CommonUtils.getRawDouble(priceUnitValueView.getText().toString());
        double counsumpt =  CommonUtils.getRawDouble(consumptionValueView.getText().toString());

        double totalFuel = (priceUnit == 0 || counsumpt == 0) ? 0 : unitFacade.getTotalFuel(counsumpt, dist);
        double totalCost = totalFuel * priceUnit;

        query.id(R.id.totalFuelValue).text(CommonUtils.formatFuel(totalFuel, unitFacade));
        query.id(R.id.totalCostValue).text(CommonUtils.formatPriceNew(totalCost, unitFacade));

        unitFacade.appendFuelUnit(  query.id(R.id.totalFuelValue).getTextView(), false);
        unitFacade.appendCurrency(  query.id(R.id.totalCostValue).getTextView(), false);
    }

    @Override
    public String getSubTitle() {
        return getString(R.string.calc);
    }

    private  class CalcTextWatcher implements  TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            calculate();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
