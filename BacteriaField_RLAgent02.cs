using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BacteriaField_RLAgent02 : MonoBehaviour
{
    [Header("Genes")]
    public float reproductionRate;
    private float rRCT;
    public int ID; //used for memory cells 
    public float healthOutside;
    public float speed;
    private Vector3 desiredPos;
    public float moveDir = 5;
    private float yPos;
    public bool replicating;
    public HostCell c;
    public Microspace ms;

    [Header("Mutations")]
    public bool cough;
    public bool TOF;
    public bool fatigue;
    public bool SL; //surface livibility
    // Start is called before the first frame update

    private float timeRep = 0;
    void Start()
    {
        yPos = 1.2f;
        desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), yPos, transform.position.z + Random.Range(-moveDir, moveDir));
    }

    // Update is called once per frame
    void Update()
    {
        if(!replicating)
        {
            transform.position = Vector3.Lerp(transform.position, desiredPos, speed * Time.deltaTime);

            if (Vector3.Distance(transform.position, desiredPos) > 0.5f) return;

            desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), yPos, transform.position.z + Random.Range(-moveDir, moveDir));
            transform.LookAt(desiredPos);
            transform.eulerAngles = new Vector3(90f, transform.rotation.y, transform.rotation.z);
        }

        clone();
    }

    private void OnCollisionEnter(Collision collision)
    {
        if (collision.gameObject.tag == "HostCell")
        {
            replicating = true;
            c = collision.gameObject.GetComponent<HostCell>();
            c.insides.Add(this);
        }
    }

    public void clone()
    {
        if(replicating)
        {
            if(timeRep > reproductionRate)
            {
                timeRep = 0;
                newBacteria();
            }
        }
    }

    public void newBacteria()
    {
        GameObject b1 = Instantiate(this.gameObject, transform.position, transform.rotation);
        BacteriaField_RLAgent02 b = b1.GetComponent<BacteriaField_RLAgent02>();
        b.replicating = true;
        b.c = this.c;

        float needsNewID = 0; //if this becomes over 1, then the bacteria mutated so much it needs a new ID.
        float ran = Random.Range(-0.5f, 0.5f);
        b.reproductionRate += ran;
        c.insides.Add(b);
        needsNewID += Mathf.Abs(ran);

        ran = Random.Range(-0.2f, 0.2f);
        b.healthOutside += ran;
        needsNewID += Mathf.Abs(ran);

        ran = Random.Range(-0.5f, 0.5f);
        b.speed += ran;
        needsNewID += Mathf.Abs(ran);

        ran = Random.Range(-0.5f, 0.5f);
        b.moveDir += ran;
        needsNewID += Mathf.Abs(ran);

        b.cough = (Random.Range(0, 101) < 5 && cough != true) ? true : false;
        b.TOF = (Random.Range(0, 101) < 5 && TOF != true) ? true : false;
        b.fatigue = (Random.Range(0, 101) < 5 && fatigue != true) ? true : false;
        b.SL = (Random.Range(0, 101) < 5 && SL != true) ? true : false;

        if(needsNewID >= 3)
        {
            b.ID = Random.Range(0, 201);
        }
    }
}
