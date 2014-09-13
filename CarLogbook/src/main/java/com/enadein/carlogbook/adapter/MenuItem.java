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
package com.enadein.carlogbook.adapter;

public class MenuItem {
    public static final int TYPE_ITEM = 0;
    public static final int HEADER = 1;

	public  int logoResId;
	public  String name;
	public int type = TYPE_ITEM;

    public MenuItem(int logoResId, String name) {
        this.logoResId = logoResId;
        this.name = name;
    }


    public MenuItem(int logoResId, String name, int type) {
        this(logoResId, name);
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
