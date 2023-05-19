using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using UnityEngine;
using Random = UnityEngine.Random;

public class Prey_AI : MonoBehaviour
{
    [Header("Prey Settings")]
    public List<float> PreyStats = new List<float> {0,0,0};// size, speed, visionrange
    public bool genderOverride;
    public float hunger;
    public float thirst;
    public float moveDir; //random movement 
    public bool startingCreature;

    [Header("Reproduction Values")]
    public GameObject Egg;
    public GameObject reproductionEffect;
    public float reproductionNeed;
    public bool canReproduce = true;
    public bool isAGuy;
    private GameObject mate;
    private bool mateSuperiority;
    public string preyTag;
    //public GameObject[] bodyParts; //for changing color


    [Header("Action Values")]
    public bool action = false;
    private bool actionDB = false;
    private string actionName = "";
    private Vector3 desiredPos;

    [Header("DNA")]
    public int maxlength = 4;
    public List<string> DNA = new List<string>();
    private List<string> codons = new List<string>() { "A", "T", "C", "G" };
    private Dictionary<string, string> geneticCode = new Dictionary<string, string>()
    {
        {"AAA", "0.1"}, {"AAC", "-0.1"}, {"AAG", "0.1"}, {"AAT", "-0.1"},
        {"ACA", "0.5"}, {"ACC", "0.5"}, {"ACG", "0.5"}, {"ACT", "0.5"},
        {"AGA", "-0.5"}, {"AGC", "0.2"}, {"AGG", "-0.5"}, {"AGT", "0.2"},
        {"ATA", "0.1"}, {"ATC", "0.1"}, {"ATG", "START"}, {"ATT", "0.3"},
        {"CAA", "0.3"}, {"CAC", "0.2"}, {"CAG", "0.3"}, {"CAT", "0.2"},
        {"CCA", "0.3"}, {"CCC", "0.3"}, {"CCG", "0.3"}, {"CCT", "0.3"},
        {"CGA", "0.1"}, {"CGC", "0.1"}, {"CGG", "0.1"}, {"CGT", "0.1"},
        {"CTA", "-0.1"}, {"CTC", "-0.1"}, {"CTG", "-0.1"}, {"CTT", "-0.1"},
        {"GAA", "0.6"}, {"GAC", "0.7"}, {"GAG", "0.6"}, {"GAT", "0.7"},
        {"GCA", "-0.6"}, {"GCC", "-0.6"}, {"GCG", "-0.6"}, {"GCT", "-0.6"},
        {"GGA", "-0.7"}, {"GGC", "-0.7"}, {"GGG", "-0.7"}, {"GGT", "-0.7"},
        {"GTA", "0.4"}, {"GTC", "0.4"}, {"GTG", "0.4"}, {"GTT", "0.4"},
        {"TAA", "STOP"}, {"TAC", "-0.4"}, {"TAG", "STOP"}, {"TAT", "-0.4"},
        {"TCA", "0.5"}, {"TCC", "0.5"}, {"TCG", "0.5"}, {"TCT", "0.5"},
        {"TGA", "STOP"}, {"TGC", "-0.5"}, {"TGG", "0"}, {"TGT", "-0.5"},
        {"TTA", "0.7"}, {"TTC", "-0.7"}, {"TTG", "0.7"}, {"TTT", "0.-7"}
    };

    public void Awake()
    {
        for (int i = 0; i < PreyStats.Count; i++)
        {
            DNA.Add(generateDNA(maxlength));
            Debug.Log(translate(DNA[i]));
            PreyStats[i] = translate(DNA[i]);
            do
            {
                if (PreyStats[i] == 0)
                {
                    DNA[i] = generateDNA(maxlength);
                    PreyStats[i] = translate(DNA[i]);
                }
            } while (PreyStats[i] == 0);
        }
    }
    public void Start()
    {
        SCDR();
        if (!genderOverride)
        {
            int rng = Random.Range(0, 2);
            isAGuy = (rng == 0) ? true : false;
        }
        gameObject.tag = (isAGuy) ? "MalePrey" : "FemalePrey";
        preyTag = (isAGuy) ? "FemalePrey" : "MalePrey";
        mateSuperiority = (isAGuy) ? false : true;


        desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), gameObject.transform.position.y, transform.position.z + Random.Range(-moveDir, moveDir));
        transform.localScale = new Vector3(PreyStats[1] + 0.5f, PreyStats[1] + 0.5f, PreyStats[1] + 0.5f);
        gameObject.GetComponent<SphereCollider>().radius = PreyStats[2] * 4;
        hunger = (float)(1 + PreyStats[0])*20;
        thirst = (float)(1 + PreyStats[0])*20;
    }

    private void Update()
    {
        Move();
        hunger -= (gameObject.GetComponent<Transform>().localScale.x * gameObject.GetComponent<Transform>().localScale.y) * 0.02f;
        thirst -= (gameObject.GetComponent<Transform>().localScale.x * gameObject.GetComponent<Transform>().localScale.y) * 0.025f;
        reproductionNeed += 0.05f;

        if (hunger <= 0 || thirst <= 0)
        {
            gameObject.GetComponent<Transform>().Rotate(0, 0, 90, Space.Self);
            gameObject.GetComponent<Rigidbody>().AddForce(Vector3.up * 10, ForceMode.Impulse);
            gameObject.GetComponent<BoxCollider>().enabled = false;
            gameObject.GetComponent<Prey_AI>().enabled = false;
        }
    }

    private string generateDNA(int length)
    {
        string dnaSequence = "ATG";
        for (int i = 0; i < length; i++)
        {
            string randomCodon = "";
            for (int j = 0; j < 3; j++)
            {
                int randomIndex = Random.Range(0, codons.Count);
                string randomNucleotide = codons[randomIndex];
                randomCodon += randomNucleotide;
            }

            string effect = geneticCode[randomCodon];
            dnaSequence += randomCodon;
        }

        return dnaSequence;
    }

    public float translate(string sequence)
    {
        float res = 0;
        bool start = false;
        for (int i = 0; i < sequence.Length; i+=3)
        {
            string codon = sequence.Substring(i, 3);
            if (geneticCode[codon].Equals("START"))
            {
                start = true;
                continue;
            }
            else if (start && (float.TryParse(geneticCode[codon], out float result)))
            {
                res += result;
            }
            if (geneticCode[codon].Equals("STOP"))
            {
                return res < 0 ? 0 : res;
            }

        }
        return res<0 ? 0 : res;
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

    private void OnTriggerEnter(Collider other)
    {
        Debug.Log(other.gameObject.name);
        if (other.gameObject.tag == "Food" || other.gameObject.tag == "Water" || other.gameObject.tag == preyTag)
        {
            changeTarget(other.gameObject);
        }
    }

    public void Move()
    {
        transform.position = Vector3.Lerp(transform.position, desiredPos, PreyStats[0] * Time.deltaTime);


        if(Vector3.Distance(transform.position, desiredPos) < 1f)
        {
            if (action)
            {
                if (actionDB)
                {
                    if (actionName == "Eat")
                    {
                        StartCoroutine(Eating());
                    }
                    else if (actionName == "Drink")
                    {
                        StartCoroutine(Drinking());
                    }
                    else if (actionName == "Mating" && mateSuperiority)
                    {
                        StartCoroutine(Mating());
                    }
                }
            }
            else
            {
                desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), gameObject.transform.position.y, transform.position.z + Random.Range(-moveDir, moveDir));
                transform.LookAt(desiredPos);
            }
        }
    }

    public void SCDR()
    {
        StartCoroutine(startCDReproduction());
    }
    IEnumerator Mating()
    {
        GameObject a = Instantiate(reproductionEffect, gameObject.transform.position, Quaternion.identity);
        canReproduce = false;
        
        Prey_AI clone = mate.GetComponent<Prey_AI>();
        clone.action = true;
        clone.canReproduce = false;

        clone.actionDB = true;
        action = true;
        actionDB = true;
        
        yield return new WaitForSeconds(5);
        Destroy(a);
        StartCoroutine(startCDReproduction());
        clone.SCDR();
        spawnEgg(clone);
        clone.mate = null;
        mate = null;
        action = false;
        actionDB = false;
        clone.action = false;
        clone.actionDB = false;
    }

    IEnumerator startCDReproduction()
    {
        yield return new WaitForSeconds(Random.Range(15, 30));
        canReproduce = true;
    }


    public void spawnEgg(Prey_AI mate)
    {
        Egg s = Instantiate(Egg, transform.position, Quaternion.identity).GetComponent<Egg>();
        s.pDNA1 = DNA;
        s.size1 = PreyStats[1];
        s.visionRange1 = PreyStats[2];
        s.moveDir1 = moveDir;

        s.pDNA2 = mate.DNA;
        s.size2 = mate.PreyStats[1];
        s.visionRange2 = mate.PreyStats[2];
        s.moveDir2 = mate.moveDir;
    }

    IEnumerator Eating()
    {
        actionDB = true;
        hunger += 10;
        yield return new WaitForSeconds(3);
        hunger += 20;
        actionDB = false;
        action = false;

    }

    IEnumerator Drinking()
    {
        actionDB = true;
        thirst += 10;
        yield return new WaitForSeconds(3);
        thirst += 20;
        actionDB = false;
        action = false;
    }

    public void changeTarget(GameObject obj)
    {

        if (obj.tag == "Food")
        {
            if(action && hunger < thirst)
            {
                Debug.Log("Hunger");
                desiredPos = obj.transform.position;
                action = true;
                actionName = "Eat";
            }
            else if(!action)
            {
                Debug.Log("Hunger");
                desiredPos = obj.transform.position;
                action = true;
                actionName = "Eat";
            }

        }
        else if (obj.tag == "Water")
        {
        if(action && thirst < hunger)
        {
                Debug.Log("Thirst");
                action = true;
                desiredPos = obj.transform.position;
                actionName = "Drink";
            } else if(!action) {
                Debug.Log("Thirst");
                action = true;
                desiredPos = obj.transform.position;
                actionName = "Drink";
            }
        }
        else if (reproductionNeed > hunger && reproductionNeed > thirst && obj.tag == preyTag && canReproduce)
        {
            Debug.Log("Mate");
            action = true;
            desiredPos = obj.transform.position;
            actionName = "Mating";
            mate = obj;

        }
    }
}
