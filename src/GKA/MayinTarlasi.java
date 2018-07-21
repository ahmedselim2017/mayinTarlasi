package GKA;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MayinTarlasi extends Application {


    private static final int HUCRE_BOYUTU=40;

    private static final int GENISLIK=800;
    private static final int YUKSEKLIK=600;

    private static final int X_HUCRE=GENISLIK/HUCRE_BOYUTU;
    private static final int Y_HUCRE=YUKSEKLIK/HUCRE_BOYUTU;

    private Scene sahne;
    private Hucre[][] grid=new Hucre[X_HUCRE][Y_HUCRE];


    public static void main(String Args[]){
        launch(Args);
    }

    @Override
    public void start(Stage primaryStage) {
        sahne=new Scene(icerikUret());




        primaryStage.setScene(sahne);
        primaryStage.show();
    }

    private Parent icerikUret(){
        Pane root=new Pane();
        root.setPrefSize(GENISLIK,YUKSEKLIK);

        for (int y=0;y<Y_HUCRE;y++){
            for (int x=0;x<X_HUCRE;x++){
                Hucre hucre=new Hucre(x,y,Math.random()<0.1);

                grid[x][y]=hucre;
                root.getChildren().add(hucre);
            }
        }

        for (int y=0;y<Y_HUCRE;y++){
            for (int x=0;x<X_HUCRE;x++){
                Hucre hucre=grid[x][y];

                if (hucre.bombaMı){
                    continue;
                }

                long bombalar=komsulariGetir(hucre).stream().filter(h->h.bombaMı).count();

                if (bombalar>0) {
                    hucre.durum.setText(String.valueOf(bombalar));
                }
            }
        }


        return root;
    }


    private List<Hucre> komsulariGetir(Hucre hucre){
        List<Hucre> komsular=new ArrayList<Hucre>();

        /*
            hhh
            hXh
            hhh
         */

        int[] puanlar=new int[]{
            -1,-1,
            -1,0,
            -1,1,
            0,-1,
            0,1,
            1,-1,
            1,0,
            1,1
        };

        final int uzunluk=puanlar.length;
        int deltaX;
        int deltaY;
        int yeniX;
        int yeniY;
        for(int i=0; i<uzunluk;i++){
            deltaX=puanlar[i];
            deltaY=puanlar[++i];

            yeniX=hucre.x+deltaX;
            yeniY=hucre.y+deltaY;

            if ((yeniX>=0&&yeniX<X_HUCRE)&&(yeniY>=0&&yeniY<Y_HUCRE)){
                komsular.add(grid[yeniX][yeniY]);
            }
        }

        return komsular;
    }


    private class Hucre extends StackPane{

        private int x,y ;
        private int bombolar;
        private boolean bombaMı;
        private boolean acikMi=false;
        private boolean soruIsaretliMi=false;
        private String geciciDeger="";

        private Rectangle cerceve=new Rectangle(HUCRE_BOYUTU-2,HUCRE_BOYUTU-2);

        private Text durum=new Text();

        public Hucre(int x,int y,boolean bombaMı){
            this.x=x;
            this.y=y;
            this.bombaMı=bombaMı;


            cerceve.setStroke(Color.LIGHTBLUE);
            cerceve.setFill(Color.DARKGRAY);
            durum.setFill(Color.WHITE);
            durum.setFont(Font.font(18));
            if (bombaMı){
                durum.setText("X");
            }
            else{
                durum.setText("");
            }

            durum.setVisible(false);
            getChildren().addAll(cerceve,durum);

            setTranslateX(x*HUCRE_BOYUTU);
            setTranslateY(y*HUCRE_BOYUTU);

            setOnMouseClicked(e->{
                if (e.getButton().toString().equals("PRIMARY")){
                    hucreyiAc();
                }
                else{
                    soruIsaretiBirak();
                }
            });
        }

        public void soruIsaretiBirak(){

            if (acikMi){
                return;
            }

            if (soruIsaretliMi){
                durum.setText(geciciDeger);
                soruIsaretliMi=false;
                durum.setVisible(false);
            }
            else{
                soruIsaretliMi=true;
                geciciDeger=durum.getText();
                durum.setText("?");
                durum.setVisible(true);
            }

        }

        public void hucreyiAc(){
            if (acikMi){
                return;
            }

            if (this.bombaMı){
               System.out.println("KAYBETTİN");
                sahne.setRoot(icerikUret());
                return;
            }

            acikMi=true;
            durum.setFill(Color.BLACK);
            durum.setVisible(true);
            cerceve.setFill(Color.LIGHTGRAY);

            if (durum.getText().isEmpty()){
                komsulariGetir(this).forEach(Hucre::hucreyiAc);
            }
        }
    }

}
