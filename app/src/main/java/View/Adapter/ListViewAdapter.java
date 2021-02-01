package View.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Objects;

import Model.Stock;
import Module.DBA;
import View.MainActivity;
import View.Dialog.PurchasePriceDialog;
import ViewModel.MainViewModel;

import com.needfor.stockoverlay.R;


public class ListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    //private ArrayList<Stock> listViewItemList = new ArrayList<Stock>() ;
    private final ArrayList<Stock> listViewItemList = new ArrayList<Stock>();
    // ListViewAdapter의 생성자
    public ListViewAdapter() { }

    private TextView targetProfit;
    private TextView purchasePrice;
    private MainViewModel mainViewModel;

    private static int POWER_ON_FLAG = 0;
    // Setter
    public void setMainViewModel(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴.
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // pos
    // ition에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_list_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView StockName = (TextView) convertView.findViewById(R.id.stockname);
        TextView StockCode = (TextView) convertView.findViewById(R.id.stockcode);
        TextView CurrentPrice = (TextView) convertView.findViewById(R.id.currentprice);
        TextView ChangePrice = (TextView) convertView.findViewById(R.id.changeprice);
        TextView ChangeRate = (TextView) convertView.findViewById(R.id.changerate);
        TextView Change = (TextView) convertView.findViewById(R.id.change);
        if(MainActivity.PURCHASE_PRICE_INPUT_FLAG == 1) {
            purchasePrice = (TextView) convertView.findViewById(R.id.list_purchaseprice);
            targetProfit = (TextView) convertView.findViewById(R.id.list_targetprice);
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Stock listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        StockName.setText(listViewItem.getName());
        StockCode.setText(listViewItem.getStockCode());
        CurrentPrice.setText(listViewItem.getCurrentPrice());
        ChangePrice.setText(listViewItem.getChangePrice());
        ChangeRate.setText(listViewItem.getChangeRate());
        Change.setText(listViewItem.getChange());
        if(MainActivity.PURCHASE_PRICE_INPUT_FLAG == 1) {
            ArrayList<String> purchasePrices = new DBA().getPurchasePrice(parent.getContext().getDatabasePath("User"));
            if (purchasePrices.size() > position && !purchasePrices.get(position).equals("-")) {
                listViewItem.setPurchasePrice(purchasePrices.get(position));
                listViewItem.setProfitAndLoss();

                int idx = 0;
                for (Stock s : Objects.requireNonNull(mainViewModel.getStockList().getValue())) {
                    if (s.getName().equals(listViewItem.getName())) break;
                    if (mainViewModel.getStockList().getValue().size() != idx + 1) idx++;
                }

                ArrayList<Stock> stocklist = mainViewModel.getStockList().getValue();
                stocklist.set(idx, listViewItem);
                mainViewModel.getStockList().setValue(stocklist);
            }

            if (listViewItem.getPurchasePrice() != null) {
                purchasePrice.setText(listViewItem.getProfitChange() + listViewItem.getProfitAndLoss());
            } else {
                purchasePrice.setText("매입가");
            }
            
            if (listViewItem.getTargetProfit() != null) {
                targetProfit.setText(listViewItem.getTargetProfit());
            } else {
                targetProfit.setText("목표수익");
            }
            targetProfit.setTextColor(Color.parseColor("#BD8F4D"));
        }

        // 텍스트 색상 변경
        StockName.setTextColor(Color.WHITE);
        StockCode.setTextColor(Color.LTGRAY);
        if(Change.getText().equals("▲")) {
            CurrentPrice.setTextColor(Color.parseColor("#F84747"));
            Change.setTextColor(Color.parseColor("#F84747"));
            ChangeRate.setTextColor(Color.parseColor("#F84747"));
            ChangePrice.setTextColor(Color.parseColor("#F84747"));
        }else if(Change.getText().equals("▼")) {
            CurrentPrice.setTextColor(Color.parseColor("#87CEFA"));
            Change.setTextColor(Color.parseColor("#87CEFA"));
            ChangeRate.setTextColor(Color.parseColor("#87CEFA"));
            ChangePrice.setTextColor(Color.parseColor("#87CEFA"));
        }else {
            CurrentPrice.setTextColor(Color.LTGRAY);
            Change.setTextColor(Color.LTGRAY);
            ChangeRate.setTextColor(Color.LTGRAY);
            ChangePrice.setTextColor(Color.LTGRAY);
        }
        if(MainActivity.PURCHASE_PRICE_INPUT_FLAG == 1) {
            if (listViewItem.getProfitChange() == null) {
                purchasePrice.setTextColor(Color.LTGRAY);
            } else if (listViewItem.getProfitChange().equals("▲")) {
                purchasePrice.setTextColor(Color.parseColor("#F84747"));
            } else {
                purchasePrice.setTextColor(Color.parseColor("#87CEFA"));
            }
        }

        if(MainActivity.PURCHASE_PRICE_INPUT_FLAG == 1) {
            LinearLayout stockArea = (LinearLayout) convertView.findViewById(R.id.listview_stockarea);
            stockArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PurchasePriceDialog purchasePriceDialog = new PurchasePriceDialog(context);
                    purchasePriceDialog.callFunction(mainViewModel, listViewItem);
                }
            });
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 임시 함수
    public void addItem(String stockname, String stockcode, String currentprice, String changeprice
        , String changerate, String change) {
        Stock item = new Stock();

        item.setName(stockname);
        item.setStockCode(stockcode);
        item.setCurrentPrice(currentprice);
        item.setChangePrice(changeprice);
        item.setChangeRate(changerate);
        item.setChange(change);

        listViewItemList.add(item);
    }

    public void setItem(ArrayList<Stock> stocks) {
        if(stocks.size() < listViewItemList.size()) {
            int flag = 0;
            for(int i=0; i<listViewItemList.size(); i++) {
                for(int k=0; k<stocks.size(); k++) {
                    if(listViewItemList.get(i).getName().equals(stocks.get(k).getName())) break;
                    if(k == stocks.size()-1) flag = 1;
                }
                if(flag == 1) {
                    Log.d("수정된 종목 삭제", listViewItemList.get(i).getName());
                    listViewItemList.remove(i);
                    flag = 0;
                }
            }
        }else if(stocks.size() > listViewItemList.size()) {
            for(int i=listViewItemList.size(); i<stocks.size(); i++) {
                Log.d("수정된 종목 추가", stocks.get(i).getName());
                listViewItemList.add(stocks.get(i));
            }
        }else {
            for(int i=0; i<stocks.size(); i++) if(listViewItemList.get(i) != stocks.get(i)) {
                Log.d("수정 사항 반영", "사이즈는 그대로");
                listViewItemList.set(i, stocks.get(i));
            }
        }

        for(int i = 0; i < listViewItemList.size(); i++){
            listViewItemList.get(i).setChange(stocks.get(i).getChange());
            listViewItemList.get(i).setChangePrice(stocks.get(i).getChangePrice());
            listViewItemList.get(i).setChangeRate(stocks.get(i).getChangeRate());
            listViewItemList.get(i).setCurrentPrice(stocks.get(i).getCurrentPrice());
            this.notifyDataSetChanged();
        }
    }
}