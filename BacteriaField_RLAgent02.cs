using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BacteriaField_RLAgent02 : MonoBehaviour
{
    private Vector3 minBoundaries;
    private Vector3 maxBoundaries;

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
        ms.BacteriaID.Add(this.ID);

        yPos = transform.position.y;
        desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), yPos, transform.position.z + Random.Range(-moveDir, moveDir));

        Vector3 parentSize = transform.parent.GetComponent<Renderer>().bounds.size;
        minBoundaries = transform.parent.position - parentSize / 2f;
        maxBoundaries = transform.parent.position + parentSize / 2f;
    }

    // Update is called once per frame
    void Update()
    {
        if (!replicating)
        {
            if (Vector3.Distance(transform.position, desiredPos) <= 0.5f)
            {
                float offsetX = Random.Range(-moveDir, moveDir);
                float offsetZ = Random.Range(-moveDir, moveDir);

                // Calculate the new desired position with the offsets and ensure it stays within the boundaries
                float newX = Mathf.Clamp(transform.position.x + offsetX, minBoundaries.x, maxBoundaries.x);
                float newZ = Mathf.Clamp(transform.position.z + offsetZ, minBoundaries.z, maxBoundaries.z);

                desiredPos = new Vector3(newX, yPos, newZ);
            }
            transform.position = Vector3.Lerp(transform.position, desiredPos, speed * Time.deltaTime);
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
        if (replicating)
        {
            timeRep += Time.deltaTime;
            if (timeRep > reproductionRate)
            {
                timeRep = 0;
                newBacteria();
            }
        }
    }

    public void newBacteria()
    {
        GameObject b1 = Instantiate(this.gameObject, transform.position, transform.rotation, transform.parent);
        BacteriaField_RLAgent02 b = b1.GetComponent<BacteriaField_RLAgent02>();
        b.replicating = true;
        b.timeRep = 0;
        b.c = this.c;
        b.ms = this.ms;
        ms.allBacteria.Add(b);


        // Calculate the new desired position with the offsets and ensure it stays within the boundaries
        float newX = Mathf.Clamp(transform.position.x + Random.Range(-0.1f, 0.1f), minBoundaries.x, maxBoundaries.x);
        float newZ = Mathf.Clamp(transform.position.z + Random.Range(-0.1f, 0.1f), minBoundaries.z, maxBoundaries.z);

        b.transform.position = new Vector3(newX, yPos, newZ);
        float needsNewID = 0; //if this becomes over 1, then the bacteria mutated so much it needs a new ID.
        float ran = Random.Range(-0.5f, 0.5f);
        b.reproductionRate += ran;
        Mathf.Abs(b.reproductionRate);
        c.insides.Add(b);
        needsNewID += Mathf.Abs(ran);

        ran = Random.Range(-0.2f, 0.2f);
        b.healthOutside += ran;
        Mathf.Abs(b.healthOutside);
        needsNewID += Mathf.Abs(ran);

        ran = Random.Range(-0.5f, 0.5f);
        b.speed += ran;
        Mathf.Abs(b.speed);
        needsNewID += Mathf.Abs(ran);

        ran = Random.Range(-0.5f, 0.5f);
        b.moveDir += ran;
        Mathf.Abs(b.moveDir);
        needsNewID += Mathf.Abs(ran);

        b.cough = (Random.Range(0, 101) < 2 && cough != true) ? true : false;
        b.TOF = (Random.Range(0, 101) < 2 && TOF != true) ? true : false;
        b.fatigue = (Random.Range(0, 101) < 2 && fatigue != true) ? true : false;
        b.SL = (Random.Range(0, 101) < 2 && SL != true) ? true : false;

        ms.BacteriaID.Add(b.ID);

        if (needsNewID >= 1.2f)
        {
            b.ID = Random.Range(0, 201);
        }
    }
}
