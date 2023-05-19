using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Microspace : MonoBehaviour
{
    [Header("Cells/Field")]
    public List<HostCell> allCells = new List<HostCell>();
    public List<BacteriaField_RLAgent02> allBacteria = new List<BacteriaField_RLAgent02>();

    public GameObject whiteBloodCell;
    private bool canRush = false;
    public int cellReplicationRate = 20;
    public int numCells;
    public bool infected = false;
    public List<int> BacteriaID = new List<int>();
    public List<int> Blacklisted = new List<int>();
    public Prey_AI pr;

    [Header("Immune Defense")]
    public float complementSystem = 10;
    public GameObject microspace;
    public float thymusSpawnRate = 10; //is affected by age and energy
    private float tymuisSpawnTime = 0f;
    public float DCT = 20; //seconds it takes for a dendritic cell to collect samples
    public float DCC = 10; //seconds it takes for a dentritic cell to find and collide with a t-cell
    public float BCSR = 5f; //antibodies kill 1 bacteria every __ seconds
    private float bcsrr = 0f;
    public Transform wbcsp;
    private bool tb;

    public GameObject dendriticCell;
    public GameObject TCell;
    public GameObject BCell;

    public GameObject innerBacteria;

    public void Update()
    {
        float a = (pr.hunger > 20) ? 10 : 17.5f;
        thymusSpawnRate = a; //this makes 0 sense
        repair();
        cs();

        tymuisSpawnTime += Time.deltaTime;
        if(tymuisSpawnTime > thymusSpawnRate)
        {
            tymuisSpawnTime = 0;
            WhiteBloodCell s = Instantiate(whiteBloodCell, wbcsp.position, Quaternion.identity, microspace.transform).GetComponent<WhiteBloodCell>();
            s.ms = this;
        }

        if (allBacteria.Count == 0)
        {
            infected = false;
            canRush = false;
        }

        bool allNull = true;

        foreach (BacteriaField_RLAgent02 obj in allBacteria)
        {
            if (obj != null)
            {
                allNull = false;
                break;
            }
        }

        if (allNull)
        {
            infected = false;
            canRush = false;
        }


        bcsrr += Time.deltaTime;

        if(bcsrr > BCSR)
        {
            for(int i = 0; i < allBacteria.Count; i++)
            {
                if (allBacteria[i] != null)
                {
                    for(int j = 0; j < Blacklisted.Count; j++)
                    {
                        if (allBacteria[i].ID == Blacklisted[j])
                        {
                            Destroy(allBacteria[i].gameObject);
                            bcsrr = 0;
                            break;
                        }
                    }
                }
            }

            bcsrr = 0;
        }

        

    }

    public IEnumerator finalRush()
    {
        yield return new WaitForSeconds(5);
        if(infected)
        {
            DendriticCell s = Instantiate(dendriticCell, wbcsp.position, Quaternion.identity, microspace.transform).GetComponent<DendriticCell>();
            s.ms = this;
            yield return new WaitForSeconds(20);
            yield return new WaitForSeconds(10);
            DendriticCell w = Instantiate(TCell, wbcsp.position, Quaternion.identity, microspace.transform).GetComponent<DendriticCell>();
            DendriticCell w1 = Instantiate(TCell, wbcsp.position, Quaternion.identity, microspace.transform).GetComponent<DendriticCell>();
            w.ms = this;
            w1.ms = this;
            yield return new WaitForSeconds(10);
            DendriticCell w2 = Instantiate(BCell, wbcsp.position, Quaternion.identity, microspace.transform).GetComponent<DendriticCell>();
            for(int i = 0; i < BacteriaID.Count; i++)
            {
                Blacklisted.Add(BacteriaID[i]);
            }
            tb = true;
            w2.ms = this;
        }
    }

    public void cs()
    {
        if (infected)
        {
            complementSystem += Time.deltaTime;
            if (complementSystem > 10)
            {
                int rng = Random.Range(0, allBacteria.Count);
                GameObject toDestroy = allBacteria[rng].gameObject;
                allBacteria.RemoveAt(rng);
                Destroy(toDestroy);
                complementSystem = 0;

                if(!canRush)
                {
                    canRush = true;
                    StartCoroutine(finalRush());
                }
            }
        }
    }

    public void infection(BacteriaAI bai)
    {
        GameObject s = Instantiate(innerBacteria, microspace.transform.position, Quaternion.identity, microspace.transform);
        BacteriaField_RLAgent02 b = s.GetComponent<BacteriaField_RLAgent02>();

        b.reproductionRate = bai.reproductionRate;
        b.ID = bai.ID;
        b.healthOutside = bai.healthOutside;
        b.speed = bai.speed;
        b.moveDir = bai.moveDir;

        b.cough = bai.cough;
        b.TOF = bai.TOF;
        b.fatigue = bai.fatigue;
        b.SL = bai.SL;


    }

    public void repair()
    {
        if (infected == false && numCells < 20)
        {
            int numRepairs = (20 - allCells.Count) / 2 + 1;
            for (int i = 0; i < numRepairs; i++)
            {
                if(allCells[i] != null)
                {
                    allCells[i].canReplicate = true;
                }
                else
                {
                    numRepairs++;
                }
            }
        }
        else if (infected == true || numCells >= 20)
        {
            for (int i = 0; i < allCells.Count; i++)
            {
                if(allCells[i] != null)
                {
                    allCells[i].canReplicate = false;

                }
            }
        }
    }
}
