using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Egg : Prey_AI
{
    [Header("Egg Values From Parent #1")]
    public List<string> pDNA1 = new List<string>();
    public float size1;
    public float visionRange1;
    public float moveDir1; //random movement 
    //public Color32 color1;

    [Header("Egg Values From Parent #2")]
    public List<string> pDNA2 = new List<string>();
    public float size2;
    public float visionRange2;
    public float moveDir2; //random movement 
    // Color32 color2;

    [Header("Config and Settings")]
    public float minTime, maxTime;
    private float maxTimeC;
    private float currentTime;
    public GameObject preyObject;
    public List<string> DNA = new List<string>();

    // Start is called before the first frame update
    void Start()
    {
        maxTimeC = Random.Range(minTime, maxTime);
    }

    public void incubate()
    {
        currentTime += Time.deltaTime;

        if(currentTime > maxTimeC)
        {
            hatch();
        }
    }

    private string recombine(string seq1, string seq2, int recombineAmt)
    {
        string sequence = "";
        List<int> crossoverPoints = new List<int>();
        for (int i = 0; i < recombineAmt; i++)
        {
            crossoverPoints.Add(UnityEngine.Random.Range(0, seq1.Length));
        }
        crossoverPoints.Sort();

        int start = 0;
        for (int i = 0; i < crossoverPoints.Count; i++)
        {
            int end = crossoverPoints[i];
            if (i % 2 == 0)
            {
                sequence += seq1.Substring(start, end - start);
            }
            else
            {
                sequence += seq2.Substring(start, end - start);
            }
            start = end;
        }

        if (crossoverPoints.Count % 2 == 0)
        {
            sequence += seq1.Substring(start);
        }
        else
        {
            sequence += seq2.Substring(start);
        }

        return sequence;
    }
    public void hatch()
    {
        GameObject temp = Instantiate(preyObject, gameObject.transform.position, Quaternion.identity);
        Prey_AI p = temp.GetComponent<Prey_AI>();
        for(int i = 1; i<p.DNA.Count; i++)
        {
            DNA.Add(recombine(pDNA1[i], pDNA2[i], 3));
            p.PreyStats[i] = translate(DNA[i]);
        }
        p.moveDir = (moveDir1 > moveDir2) ? Random.Range(moveDir2, moveDir1) : Random.Range(moveDir1, moveDir2);
        Destroy(gameObject);

    }

    // Update is called once per frame
    void Update()
    {
        incubate();
    }
}
