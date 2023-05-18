using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Microspace : MonoBehaviour
{
    [Header("Cells/Field")]
    public List<HostCell> allCells = new List<HostCell>();
    public List<BacteriaField_RLAgent02> allBacteria = new List<BacteriaField_RLAgent02>();
    public int cellReplicationRate = 20;
    public int numCells;
    public bool infected = false;
    public List<int> BacteriaID = new List<int>();
    public Prey_RLAgent01 pr;

    [Header("Immune Defense")]
    public float complementSystem = 10;
    public float thymusSpawnRate = 10; //is affected by age and energy
    public int WBT = 10; // WBTK / 5 = amount to kill (rounds down) every WBT seconds
    public int WBSR = 10; //seconds after a white blood cell implodes
    public float DCT = 30; //seconds it takes for a dendritic cell to collect samples
    public float DCC = 30; //seconds it takes for a dentritic cell to find and collide with a t-cell
    public float HTCBT = 1.4f; //increases WBT by 1.4x and -1 for WBTK
    public float BCSR = 3f; //antibodies kill 1 bacteria every __ seconds


    public void Update()
    {
        thymusSpawnRate = 10 - pr.energy; //this makes 0 sense
        repair();
        cs();
    }
    
    public void cs(){
        if(infected){
            complementSystem += Time.deltaTime;
            if(complementSystem > 10){
                int rng = Random.Range(0, allBacteria.Count);
                GameObject toDestroy = allBacteria[rng].gameObject;
                allBacteria.removeAt(rng);
                Destory(toDestroy);
                complementSystem = 0;
            }
        }
    }

    public void repair()
    {
        if (infected == false && allCells.Count < 20)
        {
            int numRepairs = (20 - allCells.Count) / 2;
            for (int i = 0; i < numRepairs; i++)
            {
                allCells[i].canReplicate = true;
            }
        }
        else if (infected == true || allCells.Count >= 20)
        {
            for (int i = 0; i < allCells.Count; i++)
            {
                allCells[i].canReplicate = false;
            }
        }
    }
}
