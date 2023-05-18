using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HostCell : MonoBehaviour
{
    public Microspace ms;
    [Header("Values")]
    public bool canReplicate = false;
    public bool controlled;
    public int bacteriaInside = 0;
    public int maxBacteria = 20;
    public List<BacteriaField_RLAgent02> insides = new List<BacteriaField_RLAgent02>();
    private int relicationTime = 30;
    private float repTimer = 0.00f;

    public void Update()
    {
        if (bacteriaInside >= maxBacteria)
        {
            for (int i = 0; i < insides.Count; i++)
            {
                insides[i].replicating = false;
            }
            ms.numCells--;
            Destroy(gameObject);
        }
    }
}
