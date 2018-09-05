/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 2012 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	QuickActionLib
 * filename	:	Main.java
 * author	:	isyoon
 *
 * <pre>
 * yanzm.products.quickaction.lib
 *    |_ Main.java
 * </pre>
 * 
 */

package yanzm.products.quickaction.lib;

import android.app.Activity;

//import yanzm.products.quickaction.lib.ActionItem;
//import yanzm.products.quickaction.lib.QuickAction;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.Toast;

/**
 * 
 * TODO NOTE:
 * 
 * @author isyoon
 * @since 2012. 8. 1.
 * @version 1.0
 * @see Main.java
 */
public class QuickActionTest2 extends Activity {

	//	@Override
	//	public void onCreate(Bundle savedInstanceState) {
	//		super.onCreate(savedInstanceState);
	//		setContentView(R.layout.main);
	//
	//		// 1st item  
	//		final ActionItem chart = new ActionItem();
	//
	//		chart.setTitle("Chart");
	//		chart.setIcon(getResources().getDrawable(R.drawable.chart));
	//		chart.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				Toast.makeText(QuickActionTest2.this, "Chart selected", Toast.LENGTH_SHORT).show();
	//			}
	//		});
	//
	//		// 2nd item  
	//		final ActionItem production = new ActionItem();
	//
	//		production.setTitle("Products");
	//		production.setIcon(getResources().getDrawable(R.drawable.production));
	//		production.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				Toast.makeText(QuickActionTest2.this, "Products selected", Toast.LENGTH_SHORT).show();
	//			}
	//		});
	//
	//		// 3rd item  
	//		final ActionItem budget = new ActionItem();
	//
	//		budget.setTitle("Budget");
	//		budget.setIcon(getResources().getDrawable(R.drawable.budget));
	//		budget.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				Toast.makeText(QuickActionTest2.this, "Budget selected", Toast.LENGTH_SHORT).show();
	//			}
	//		});
	//
	//		// 4th item  
	//		final ActionItem dashboard = new ActionItem();
	//
	//		dashboard.setIcon(getResources().getDrawable(R.drawable.dashboard));
	//		dashboard.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				Toast.makeText(QuickActionTest2.this, "dashboard selected", Toast.LENGTH_SHORT).show();
	//			}
	//		});
	//
	//		// 5th item  
	//		final ActionItem users = new ActionItem();
	//
	//		users.setIcon(getResources().getDrawable(R.drawable.users));
	//		users.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				Toast.makeText(QuickActionTest2.this, "Products selected", Toast.LENGTH_SHORT).show();
	//			}
	//		});
	//
	//		// Default Setting  
	//		Button btn1 = (Button) this.findViewById(R.id.btn1);
	//		btn1.setText("Default Setting");
	//		btn1.setOnClickListener(new View.OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(chart);
	//				qa.addActionItem(production);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_AUTO  
	//		Button btn2 = (Button) this.findViewById(R.id.btn2);
	//		btn2.setText("QuickAction.ANIM_AUTO\nmany Items");
	//		btn2.setOnClickListener(new View.OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(chart);
	//				qa.addActionItem(production);
	//				qa.addActionItem(chart);
	//				qa.addActionItem(production);
	//				qa.addActionItem(chart);
	//				qa.addActionItem(production);
	//				qa.addActionItem(chart);
	//				qa.addActionItem(production);
	//				qa.setAnimStyle(QuickAction.ANIM_AUTO);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_GROW_FROM_CENTER  
	//		Button btn3 = (Button) this.findViewById(R.id.btn3);
	//		btn3.setText("QuickAction.ANIM_GROW_FROM_CENTER\nItems have no title\nNo Interpolator");
	//		btn3.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(dashboard);
	//				qa.addActionItem(users);
	//				qa.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
	//				qa.setAnimTrack(R.anim.rail2, null);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_GROW_FROM_CENTER  
	//		Button btn4 = (Button) this.findViewById(R.id.btn4);
	//		btn4.setText("QuickAction.ANIM_GROW_FROM_LEFT\nItems have no title\nNo Track Animation");
	//		btn4.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(dashboard);
	//				qa.addActionItem(users);
	//				qa.setAnimStyle(QuickAction.ANIM_GROW_FROM_LEFT);
	//				qa.setAnimTrackEnabled(false);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_GROW_FROM_CENTER  
	//		Button btn5 = (Button) this.findViewById(R.id.btn5);
	//		btn5.setText("QuickAction.ANIM_GROW_FROM_RIGHT");
	//		btn5.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(chart);
	//				qa.addActionItem(users);
	//				qa.setAnimStyle(QuickAction.ANIM_GROW_FROM_RIGHT);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_GROW_FROM_CENTER  
	//		Button btn6 = (Button) this.findViewById(R.id.btn6);
	//		btn6.setText("QuickAction.ANIM_REFLECT");
	//		btn6.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(dashboard);
	//				qa.addActionItem(users);
	//				qa.setAnimStyle(QuickAction.ANIM_REFLECT);
	//				qa.setLayoutStyle(QuickAction.STYLE_BUTTON);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		//////////////////////////////////////////////////////  
	//
	//		// Default Setting + STYLE_LIST  
	//		Button btn7 = (Button) this.findViewById(R.id.btn7);
	//		btn7.setText("QuickAction.STYLE_LIST");
	//		btn7.setOnClickListener(new View.OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(chart);
	//				qa.addActionItem(production);
	//				qa.setLayoutStyle(QuickAction.STYLE_LIST);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_AUTO  
	//		Button btn8 = (Button) this.findViewById(R.id.btn8);
	//		btn8.setText("QuickAction.ANIM_AUTO\nQuickAction.STYLE_LIST with Constractor");
	//		btn8.setOnClickListener(new View.OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v, R.layout.popup2, QuickAction.STYLE_LIST);
	//
	//				qa.addActionItem(chart);
	//				qa.addActionItem(production);
	//				qa.setAnimStyle(QuickAction.ANIM_AUTO);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_GROW_FROM_CENTER  
	//		Button btn9 = (Button) this.findViewById(R.id.btn9);
	//		btn9.setText("QuickAction.ANIM_GROW_FROM_CENTER\nQuickAction.STYLE_LIST\nsetItemLayoutId()");
	//		btn9.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(dashboard);
	//				qa.addActionItem(users);
	//				qa.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
	//				qa.setLayoutStyle(QuickAction.STYLE_LIST);
	//				qa.setItemLayoutId(R.layout.action_item);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_GROW_FROM_CENTER  
	//		Button btn10 = (Button) this.findViewById(R.id.btn10);
	//		btn10.setText("QuickAction.ANIM_GROW_FROM_LEFT");
	//		btn10.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(budget);
	//				qa.addActionItem(production);
	//				qa.setAnimStyle(QuickAction.ANIM_GROW_FROM_LEFT);
	//				qa.setLayoutStyle(QuickAction.STYLE_LIST);
	//				qa.setItemLayoutId(R.layout.action_item2);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_GROW_FROM_CENTER  
	//		Button btn11 = (Button) this.findViewById(R.id.btn11);
	//		btn11.setText("QuickAction.ANIM_GROW_FROM_RIGHT\ncustom layout");
	//		btn11.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v, R.layout.popup2, QuickAction.STYLE_LIST);
	//
	//				qa.addActionItem(budget);
	//				qa.addActionItem(production);
	//				qa.setAnimStyle(QuickAction.ANIM_GROW_FROM_RIGHT);
	//				qa.setItemLayoutId(R.layout.action_item3);
	//
	//				qa.show();
	//			}
	//		});
	//
	//		// ANIM_GROW_FROM_CENTER  
	//		Button btn12 = (Button) this.findViewById(R.id.btn12);
	//		btn12.setText("QuickAction.ANIM_REFLECT");
	//		btn12.setOnClickListener(new OnClickListener() {
	//			@Override
	//			public void onClick(View v) {
	//				QuickAction qa = new QuickAction(v);
	//
	//				qa.addActionItem(dashboard);
	//				qa.addActionItem(users);
	//				qa.setAnimStyle(QuickAction.ANIM_REFLECT);
	//				qa.setLayoutStyle(QuickAction.STYLE_LIST);
	//
	//				qa.show();
	//			}
	//		});
	//	}
}
