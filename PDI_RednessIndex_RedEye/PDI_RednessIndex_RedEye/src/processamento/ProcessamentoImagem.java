package processamento;

import java.awt.Color;
import java.lang.Math;
import java.awt.image.BufferedImage;

/**
 * @author Luiz Fillipe Oliveira Morais
 */

public class ProcessamentoImagem {

	public static BufferedImage riRedEye(BufferedImage imagem) {
		double max = -Double.MAX_VALUE;
		double min = Double.MAX_VALUE;
		double ri;

		for(int i = 0; i < imagem.getWidth(); i++) {
			for(int j = 0; j < imagem.getHeight(); j++) {
				Color c = new Color(imagem.getRGB(i, j));
				double R = new Double(c.getRed());
				double G = new Double(c.getGreen());
				double B = new Double(c.getBlue());
                                
                                double r = R / (R+G+B);
                                double g = G / (R+G+B);
                                double b = B / (R+G+B);
                                
                                
				ri = Math.pow(r,2) / (Math.pow((r+g+b+1),2));   //local da nova função
				
				if (ri < min) min = ri;
				if (ri > max) max = ri;
			}
		}
		
		for(int i = 0; i < imagem.getWidth(); i++) {
			for(int j = 0; j < imagem.getHeight(); j++) {
				Color c = new Color(imagem.getRGB(i, j));
				double R = new Double(c.getRed());
				double G = new Double(c.getGreen());
                                double B = new Double(c.getBlue());
                                
                                double r = R / (R+G+B);
                                double g = G / (R+G+B);
                                double b = B / (R+G+B);
				
				ri = Math.pow(r,2) / (Math.pow((r+g+b+1),2));

				if(((r + g) <= 0)){ 
					Color novo = new Color(0, 0, 0);
					imagem.setRGB(i, j, novo.getRGB());
				} else {
					double riNormalizado = 255 * ((ri - min) / (max - min));
					int riBW = (int) riNormalizado;

					Color novo = new Color(riBW, riBW, riBW);
					imagem.setRGB(i, j, novo.getRGB());
				}
			}
		}
		return imagem;
	}
                    /*
					// Calculo opcional para Normalização,
					// por se encaixar melhor em determinadas ocasiões

                    ri =  ri * 255;
					
                    if((ri) < 0){
					   Color newColor = new Color(0, 0, 0);
					   imagem.setRGB(i, j, newColor.getRGB());
					} else if(ri > 255){
					   Color newColor = new Color((int)(ri), (int)(ri), (int)(ri));
					   imagem.setRGB(i, j, newColor.getRGB());
					} else {
					   Color newColor = new Color((int) ri, (int) ri, (int) ri);
					   imagem.setRGB(i, j, newColor.getRGB());
					}
                    */
					
	
	public static BufferedImage OtsuBinarization(BufferedImage img) {
		
		BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		// ROTINA de de Otsu TRADICIONAL

		//Armazenando o tamanho da imagem
		int Largura = img.getWidth();// Largura da imagem
		int Altura = img.getHeight();// Altura da imagem
		int col, lin, i, cinza;
		double totalPixel= (double) Largura * Altura;
		double [] proba = new double[256];
		int [] histogram = new int[256];
		int k, uiLimiar;
			   
		// inicializacao do Histograma
		for(i=0; i < 256; i++){
			histogram[i]= 0;
		}

		// calculo do Histograma  
		for( lin = 0; lin < Altura; lin++) {
			for( col = 0; col < Largura; col++) {
				Color x = new Color(img.getRGB(col,lin));
				cinza = (int)((x.getGreen() + x.getRed() + x.getBlue())/3);
				histogram[cinza]++;
			}
		}
			
		// Aloca as Matrizes
		double fSigmaBMax, fMiTotal;
		double [] fOmega = new double[256], fMi = new double[256] , fSigmaB = new double[256];

		//Passo 2: Calculo de probabilidades
		for (i = 0; i < 256; i++) {
			proba[i] = (double) ((histogram[i])/(double)(totalPixel));
			fOmega[i] = fMi[i] = 0.0;
		}

		for (k = 0; k < 256; k++){
			for (i = 0; i < k; i++){
				fOmega[k] += proba[i];
			}
		}

		for (k = 0; k < 256; k++){
			for (i = 0; i < k; i++){
				fMi[k] += (i + 1) * proba[i];
			}
		}
	  
		fMiTotal = fSigmaBMax = 0.0;  
		uiLimiar = 128; //inicialização do valor de limiar  de Otsu

		for (i = 0; i < 256; i++){
			fMiTotal += (i + 1) * proba[i];
		}

		if ((fOmega[0] * (1 - fOmega[0])) != 0.0){
			fSigmaBMax = (  (fMiTotal * fOmega[0] - fMi[0]) * (fMiTotal * fOmega[0] - fMi[0]) ) / (fOmega[0] * (1 - fOmega[0]));
			uiLimiar = 0;
		}

		for (k = 1; k < 256; k++){
			if ((fOmega[k] * (1 - fOmega[k])) != 0.0){
			   fSigmaB[k] = (  (fMiTotal * fOmega[k] - fMi[k]) * (fMiTotal * fOmega[k] - fMi[k]) ) / (fOmega[k] * (1 - fOmega[k]));

				if (fSigmaB[k] > fSigmaBMax){
					fSigmaBMax = fSigmaB[k];
					uiLimiar = ( int) k;
				}
			}
		}
		
		// valor de limiar  de Otsu modificado para poder pegar toda a regiao da mama 
		System.out.println(uiLimiar);
		
		//Cria a  imagem  binarizada 
		// Aloca a Matriz
		int [][] pBufferbinario = new int[Altura][Largura]; //Cria um PONTEIRO para a  imagem  binarizada 

		for( lin = 0; lin < Altura; lin++) {
			for( col = 0; col < Largura; col++) {
			Color x = new Color(img.getRGB(col,lin));
				cinza = (int)((x.getGreen() + x.getRed() + x.getBlue())/3); 
				if (cinza > uiLimiar){
					pBufferbinario[lin][col] = 1;
				} else {
					//255; mudado para fazer a multiplicacao img original * binária
					pBufferbinario[lin][col] = 0;
				}
			}
		}

		//Aqui Gera a  imagem binária 
		for( lin = 0; lin < Altura; lin++) {
			for( col = 0; col < Largura; col++){
				int atual = pBufferbinario[lin][col]* 255;
				Color novo = new Color(atual, atual, atual);
				res.setRGB(col,lin, novo.getRGB());
			}
		}
		return res;
	}
        public static BufferedImage BinarizacaoEntropiaLiLee(BufferedImage img)
{
    		BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		// ROTINA de de Otsu TRADICIONAL

		//Armazenando o tamanho da imagem
		int Largura = img.getWidth();// Largura da imagem
		int Altura = img.getHeight();// Altura da imagem
		int col, lin, i, cinza;
		double totalPixel= (double) Largura * Altura;
		double [] proba = new double[256];
		int [] histogram = new int[256];
		int k, uiLimiar;
			   
		// inicializacao do Histograma
		for(i=0; i < 256; i++){
			histogram[i]= 0;
		}

		// calculo do Histograma  
		for( lin = 0; lin < Altura; lin++) {
			for( col = 0; col < Largura; col++) {
				Color x = new Color(img.getRGB(col,lin));
				cinza = (int)((x.getGreen() + x.getRed() + x.getBlue())/3);
				histogram[cinza]++;
			}
		}
	long soma1, soma2, soma3, soma4;
	long total1, total2;
	double[] mi1 = new double [256], mi2 = new double[256];
	double[] ni1 = new double [256], ni2 = new double[256];
	double[] ni = new double [256];
        	
	int l,m;

	total1	= 0;
	total2	= 0;
	for (l = 0; l < 256; l++)
	{
            ni1[l] = 0;
            ni2[l] = 0;
            total1 += l * histogram[l];
            total2 += histogram[l];
	}
	soma1	= 0;
	soma2	= 0;
	soma3	= 0;
	soma4	= 0;
	for (l = 1; l < 256; l++)
	{
		soma1 += (l-1) * histogram[l-1];
		soma2 += histogram [l-1];
		soma3 = total1 - soma1;
		soma4 = total2 - soma2;
		
		mi1[l] = (soma2 != 0) ? ((double) (soma1)) / soma2 : 0;
		mi2[l] = (soma4 != 0) ? ((double) (soma3)) / soma4 : 0;

		for (m = 1; m <   l; m++)
                    ni1[l] += (mi1[l] != 0) ? (m * histogram[m] * Math.log(m/mi1[l])) : 0.; 

		for (m = l; m < 256; m++)
                    ni2[l] += (mi2[l] != 0) ? (m * histogram[m] * Math.log(m/mi2[l])) : 0.;

		ni[l] = ni1[l] + ni2[l];
	}   
		
	// busca do minimo = limiar 
	uiLimiar = 1;
	for (l = 2; l < 256; l++)
	{
		if (ni[l] < ni[uiLimiar])
			uiLimiar = (int)l;
	}   
        
   
                
	
		//Cria a  imagem  binarizada 
		// Aloca a Matriz
		int [][] pBufferbinario = new int[Altura][Largura]; //Cria um PONTEIRO para a  imagem  binarizada 

		for( lin = 0; lin < Altura; lin++) {
			for( col = 0; col < Largura; col++) {
			Color x = new Color(img.getRGB(col,lin));
				cinza = (int)((x.getGreen() + x.getRed() + x.getBlue())/3); 
				if (cinza > uiLimiar){
					pBufferbinario[lin][col] = 1;
				} else {
					//255; mudado para fazer a multiplicacao img original * binária
					pBufferbinario[lin][col] = 0;
				}
			}
		}

		//Aqui Gera a  imagem binária 
		for( lin = 0; lin < Altura; lin++) {
			for( col = 0; col < Largura; col++){
				int atual = pBufferbinario[lin][col]* 255;
				Color novo = new Color(atual, atual, atual);
				res.setRGB(col,lin, novo.getRGB());
			}
		}
		return res;
	}
}


