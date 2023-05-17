using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HostCell : Prey_AI
{
    public Microspace ms;
    [Header("Values")]
    public bool controlled;
    public int bacteriaInside = 0;
    public int maxBacteria = 20;
    public List<BacteriaField_RLAgent02> insides = new List<BacteriaField_RLAgent02>();
    private int relicationTime = 30;
    private float repTimer = 0.00f;
    
    public void Update(){
        if(bacteriaInside >= maxBacteria){
            for(int i = 0; i < insides.Count; i++)
            {
                insides.get(i).outOfCell = true;
            }
            ms.numCells--;
            Destroy(gameObject);
        }
    }
    
    
}
