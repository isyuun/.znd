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
 * 2014 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.PLAY.TEST
 * filename	:	FileDialogFragment.java
 * author	:	isyoon
 *
 * <pre>
 * com.lamerman.isyoon
 *    |_ FileDialogFragment.java
 * </pre>
 * 
 */

package com.lamerman.isyoon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import kr.kymedia.karaoke.widget.FileAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * TODO<br>
 * 
 * <pre></pre>
 * 
 * @author isyoon
 * @since 2014. 8. 19.
 * @version 1.0
 */
class FileDialogFragment extends ListDialogFragment {

	// /**
	// * Chave de um item da lista de paths.
	// */
	// private static final String ITEM_KEY = "key";
	//
	// /**
	// * Imagem de um item da lista de paths (diretorio ou arquivo).
	// */
	// private static final String ITEM_IMAGE = "image";
	//
	// /**
	// * 파일
	// */
	// private static final String ITEM_FILE = "file";

	/**
	 * Diretorio raiz.
	 */
	protected static final String ROOT = "/";

	/**
	 * Parametro de entrada da Activity: path inicial. Padrao: ROOT.
	 */
	public static final String START_PATH = "START_PATH";

	/**
	 * Parametro de entrada da Activity: filtro de formatos de arquivos. Padrao: null.
	 */
	public static final String FORMAT_FILTER = "FORMAT_FILTER";

	/**
	 * Parametro de saida da Activity: path escolhido. Padrao: null.
	 */
	public static final String RESULT_PATH = "RESULT_PATH";

	/**
	 * Parametro de entrada da Activity: tipo de selecao: pode criar novos paths ou nao. Padrao: nao
	 * permite.
	 * 
	 * @see {@link SelectionMode}
	 */
	public static final String SELECTION_MODE = "SELECTION_MODE";

	/**
	 * Parametro de entrada da Activity: se e permitido escolher diretorios. Padrao: falso.
	 */
	public static final String CAN_SELECT_DIR = "CAN_SELECT_DIR";

	// private List<String> path = null;

	private TextView myPath;
	private EditText mFileName;
	// private ArrayList<HashMap<String, Object>> mList;

	protected Button selectButton;

	protected LinearLayout layoutSelect;
	private LinearLayout layoutCreate;
	private InputMethodManager inputManager;
	private String parentPath;

	private String currentPath = ROOT;

	public String getCurrentPath() {
		return currentPath;
	}

	private String startPath;

	public String getStartPath() {
		return startPath;
	}

	protected int selectionMode = SelectionMode.MODE_CREATE;

	private String[] formatFilter = null;

	private boolean canSelectDir = false;

	private File selectedFile;

	public File getSelectedFile() {
		return selectedFile;
	}

	private final HashMap<String, Integer> lastPositions = new HashMap<String, Integer>();

	/**
	 * Called when the activity is first created. Configura todos os parametros de entrada e das
	 * VIEWS..
	 */
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.file_dialog_main);
	@Override
	protected void onActivityCreated() {
		Log.e(toString(), getMethodName());

		super.onActivityCreated();

		myPath = (TextView) findViewById(R.id.path);
		mFileName = (EditText) findViewById(R.id.fdEditTextFile);

		inputManager = (InputMethodManager) getApplicationContext().getSystemService(
				INPUT_METHOD_SERVICE);

		selectButton = (Button) findViewById(R.id.fdButtonSelect);
		selectButton.setEnabled(false);
		selectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedFile != null) {
					getIntent().putExtra(RESULT_PATH, selectedFile.getPath());
					setResult(RESULT_OK, getIntent());
					finish();
				}
			}
		});

		final Button newButton = (Button) findViewById(R.id.fdButtonNew);
		newButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setCreateVisible(v);

				mFileName.setText("");
				mFileName.requestFocus();
			}
		});

		selectionMode = getIntent().getIntExtra(SELECTION_MODE, SelectionMode.MODE_CREATE);

		formatFilter = getIntent().getStringArrayExtra(FORMAT_FILTER);

		canSelectDir = getIntent().getBooleanExtra(CAN_SELECT_DIR, false);

		if (selectionMode == SelectionMode.MODE_OPEN) {
			newButton.setEnabled(false);
		}

		layoutSelect = (LinearLayout) findViewById(R.id.fdLinearLayoutSelect);
		layoutCreate = (LinearLayout) findViewById(R.id.fdLinearLayoutCreate);
		layoutCreate.setVisibility(View.GONE);

		final Button cancelButton = (Button) findViewById(R.id.fdButtonCancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectVisible(v);
			}

		});
		final Button createButton = (Button) findViewById(R.id.fdButtonCreate);
		createButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mFileName.getText().length() > 0) {
					getIntent().putExtra(RESULT_PATH, currentPath + "/" + mFileName.getText());
					setResult(RESULT_OK, getIntent());
					finish();
				}
			}
		});

		startPath = getIntent().getStringExtra(START_PATH);
		startPath = startPath != null ? startPath : ROOT;
		if (canSelectDir) {
			File file = new File(startPath);
			selectedFile = file;
			selectButton.setEnabled(true);
		}
		getDir(startPath);

	}

	protected void getDir(String dirPath) {

		boolean useAutoSelection = dirPath.length() < currentPath.length();

		Integer position = lastPositions.get(parentPath);

		getDirImpl(dirPath);

		if (position != null && useAutoSelection) {
			getListView().setSelection(position);
		}

	}

	/**
	 * Monta a estrutura de arquivos e diretorios filhos do diretorio fornecido.
	 * 
	 * @param dirPath
	 *          Diretorio pai.
	 */
	private void getDirImpl(final String dirPath) {

		currentPath = dirPath;

		final List<String> item = new ArrayList<String>();
		List<String> path = new ArrayList<String>();
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		File f = new File(currentPath);
		File[] files = f.listFiles();
		if (files == null) {
			currentPath = ROOT;
			f = new File(currentPath);
			files = f.listFiles();
		}
		// myPath.setText(getText(R.string.location) + ": " + currentPath);
		myPath.setText(currentPath);

		if (!currentPath.equals(ROOT)) {

			item.add(ROOT);
			addItem(list, R.drawable.folder, ROOT, false, ROOT);
			path.add(ROOT);

			item.add("../");
			addItem(list, R.drawable.folder, "../", false, f.getParent());
			path.add(f.getParent());
			parentPath = f.getParent();

		}

		TreeMap<String, String> dirsMap = new TreeMap<String, String>();
		TreeMap<String, String> dirsPathMap = new TreeMap<String, String>();
		TreeMap<String, String> filesMap = new TreeMap<String, String>();
		TreeMap<String, String> filesPathMap = new TreeMap<String, String>();
		for (File file : files) {
			if (file.isDirectory()) {
				String dirName = file.getName();
				dirsMap.put(dirName, dirName);
				dirsPathMap.put(dirName, file.getPath());
			} else {
				final String fileName = file.getName();
				final String fileNameLwr = fileName.toLowerCase(Locale.getDefault());
				// se ha um filtro de formatos, utiliza-o
				if (formatFilter != null) {
					boolean contains = false;
					for (int i = 0; i < formatFilter.length; i++) {
						final String formatLwr = formatFilter[i].toLowerCase(Locale.getDefault());
						if (fileNameLwr.endsWith(formatLwr)) {
							contains = true;
							break;
						}
					}
					if (contains) {
						filesMap.put(fileName, fileName);
						filesPathMap.put(fileName, file.getPath());
					}
					// senao, adiciona todos os arquivos
				} else {
					filesMap.put(fileName, fileName);
					filesPathMap.put(fileName, file.getPath());
				}
			}
		}
		item.addAll(dirsMap.tailMap("").values());
		item.addAll(filesMap.tailMap("").values());
		path.addAll(dirsPathMap.tailMap("").values());
		path.addAll(filesPathMap.tailMap("").values());

		int idx = 0;

		if (!currentPath.equals(ROOT)) {
			idx = 2;
		}

		for (String dir : dirsMap.tailMap("").values()) {
			String s = path.get(idx);
			addItem(list, R.drawable.folder, dir, false, s);
			idx++;
		}

		for (String file : filesMap.tailMap("").values()) {
			String s = path.get(idx);
			addItem(list, R.drawable.file, file, false, s);
			idx++;
		}

		FileAdapter fileList = new FileAdapter(getApplicationContext(), list,
				R.layout.file_dialog_row, new String[] { SelectionItem.ITEM_KEY, SelectionItem.ITEM_IMAGE,
						SelectionItem.ITEM_CHECK }, new int[] { R.id.fdrowtext, R.id.fdrowimage,
						R.id.fdrowcheck });

		// fileList.notifyDataSetChanged();
		fileList.setNotifyOnChange(false);

		setListAdapter(fileList);

	}

	// private void addItem(String fileName, int imageId) {
	// HashMap<String, Object> item = new HashMap<String, Object>();
	// item.put(SelectionItem.ITEM_KEY, fileName);
	// item.put(SelectionItem.ITEM_IMAGE, imageId);
	// mList.add(item);
	// }

	private void addItem(ArrayList<HashMap<String, Object>> list, int imageId, String fileName,
			boolean check, String path) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(SelectionItem.ITEM_KEY, fileName);
		item.put(SelectionItem.ITEM_IMAGE, imageId);
		item.put(SelectionItem.ITEM_CHECK, check);
		item.put(SelectionItem.ITEM_PATH, path);
		// _Log.e(toString(), getMethodName() + item);
		list.add(item);
	}

	/**
	 * Quando clica no item da lista, deve-se: 1) Se for diretorio, abre seus arquivos filhos; 2) Se
	 * puder escolher diretorio, define-o como sendo o path escolhido. 3) Se for arquivo, define-o
	 * como path escolhido. 4) Ativa botao de selecao.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// _Log.i(toString(), getMethodName() + path.get(position));
		super.onListItemClick(l, v, position, id);

		HashMap<String, Object> item = ((FileAdapter) getListAdapter()).getItem(position);
		String path = (String) item.get(SelectionItem.ITEM_PATH);
		Log.e(toString(), getMethodName() + item);

		File file = new File(path);

		setSelectVisible(v);

		if (file.isDirectory()) {
			selectButton.setEnabled(false);
			if (file.canRead()) {
				lastPositions.put(currentPath, position);
				getDir(path);
				if (canSelectDir) {
					selectedFile = file;
					v.setSelected(true);
					selectButton.setEnabled(true);
				}
			} else {
				new AlertDialog.Builder(getActivity()).setIcon(R.drawable.icon)
						.setTitle("[" + file.getName() + "] " + getText(R.string.cant_read_folder))
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();
			}
		} else {
			selectedFile = file;
			v.setSelected(true);
			selectButton.setEnabled(true);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			selectButton.setEnabled(false);

			if (layoutCreate.getVisibility() == View.VISIBLE) {
				layoutCreate.setVisibility(View.GONE);
				layoutSelect.setVisibility(View.VISIBLE);
			} else {
				// if (!currentPath.equals(ROOT)) {
				if (!currentPath.equals(ROOT) && !currentPath.equals(startPath)) {
					getDir(parentPath);
				} else {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Define se o botao de CREATE e visivel.
	 * 
	 * @param v
	 */
	private void setCreateVisible(View v) {
		layoutCreate.setVisibility(View.VISIBLE);
		layoutSelect.setVisibility(View.GONE);

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		selectButton.setEnabled(false);
	}

	/**
	 * Define se o botao de SELECT e visivel.
	 * 
	 * @param v
	 */
	private void setSelectVisible(View v) {
		layoutCreate.setVisibility(View.GONE);
		layoutSelect.setVisibility(View.VISIBLE);

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		selectButton.setEnabled(false);
	}

}
