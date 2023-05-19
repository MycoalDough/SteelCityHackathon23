using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HostCell : MonoBehaviour
{
    public Microspace ms;
    [Header("Values")]
    public bool canReplicate = false;
    public bool controlled;
    public int maxBacteria = 20;
    public List<BacteriaField_RLAgent02> insides = new List<BacteriaField_RLAgent02>();
    private float relicationTime = 30;
    private float repTimer = 0.00f;

    public void Start()
    {
        relicationTime = Random.Range(25f, 35f);
    }

    public void Update()
    {
        if (insides.Count >= maxBacteria)
        {
            for (int i = 0; i < insides.Count; i++)
            {
                insides[i].replicating = false;
            }
            ms.numCells--;
            Destroy(gameObject);
            ms.infected = true;
        }

        if(canReplicate)
        {

            Debug.Log("rep");
            repTimer += Time.deltaTime;

            if(repTimer > relicationTime)
            {
                GameObject s = Instantiate(gameObject, transform.position + new Vector3(Random.Range(-0.5f, 0.5f), 0, Random.Range(-0.5f, 0.5f)), Quaternion.identity, transform.parent);
                repTimer = 0;

                int nullIndex = -1;
                for (int i = 0; i < ms.allCells.Count; i++)
                {
                    if (ms.allCells[i] == null)
                    {
                        nullIndex = i;
                        break;
                    }
                }
                if (nullIndex != -1)
                {
                    ms.numCells++;
                    ms.allCells[nullIndex] = s.GetComponent<HostCell>();
                }
                else
                {
                    Destroy(s);
                }
            }
        }
        else
        {
            repTimer = 0;
        }
    }
}
