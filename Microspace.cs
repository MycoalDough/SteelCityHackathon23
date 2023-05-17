using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Microspace : MonoBehaviour
{
    [Header("Cells/Field")]
    public int numCells = 20;
    public int cellReplicationRate = 20;
    public bool infected = false;
    public List<int> BacteriaID = new List<int>();

    [Header("Immune Defense")]
    public float complementSystem = 10;
    public float thymusSpawnRate = 10; //is affected by age and energy
    public int WBTK = 5; //white blood cells to kill 1 bacteria
    public int WBT = 10; // WBTK / 5 = amount to kill (rounds down) every WBT seconds
    public int WBSR = 10; //seconds after a white blood cell implodes
    public float DCT = 30; //seconds it takes for a dendritic cell to collect samples
    public float DCC = 30; //seconds it takes for a dentritic cell to find and collide with a t-cell
    public float HTCBT = 1.4f; //increases WBT by 1.4x and -1 for WBTK
    public float BCSR = 3f; //antibodies kill 1 bacteria every __ seconds
    private 
}
