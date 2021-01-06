package Module;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Model.Stock;

public class DBAccess {
    // FireStore(Firebase) 접속용 Instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // -------------- 생성자 --------------
    /**
     * Google FireStore DB 내에서 Stock 컬렉션 Access 해서 데이터 받아오는 생성자
     * @param stock
     */
    public DBAccess(Stock stock) {
        Log.d("start", "DB access start");
        // 종목명에 맞는 데이터를 FireStore DB 중 Stock 컬렉션에서 불러온다.
        final DocumentReference docRef = db.collection("Stock").document(stock.getName());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        stock.setStockCode(document.getData().get("code").toString());
                        stock.setDetailCode(document.getData().get("detail_code").toString());
                        Log.d("This stock's data", "Stock Code: " + stock.getStockCode() + ", Detail Code: " + stock.getDetailCode());

                        /*
                        stock.setCrawlingTh(stock.getStockCode());
                        stock.getCrawlingTh().start();
                         */
                    } else {
                        Log.d("No Search", "No such document");
                    }
                } else {
                    Log.d("Failed", "get failed with ", task.getException());
                }
            }
        });
        Log.d("end", "DB access end");
    }

    // -------------- 기타 메서드 --------------
    // 회원가입 메서드
    public void signUp() {

    }

    // 관심종목 추가 메서드
    public void addInterestedStock(String name) {

    }

    // 관심종목 삭제 메서드
    public void subInterestedStock(String name) {

    }

    // 모든 관심종목 읽어오는 메서드
    public void readInterestedStock() {

    }
}
